package grupo3.ppai.gestores;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import grupo3.ppai.entidades.Bolsin;
import grupo3.ppai.entidades.ComisionMedica;
import grupo3.ppai.entidades.Empleado;
import grupo3.ppai.entidades.Sesion;
import grupo3.ppai.repositorios.BolsinRepository;
import grupo3.ppai.repositorios.EmpleadoRepository;
import grupo3.ppai.repositorios.SesionRepository;

/**
 * <<control>> GestorSeguimientoBolsines - CU 36 Consultar seguimiento de bolsines.
 *
 * Una instancia nueva por pedido (prototype, ver docs/DECISIONES.md): los atributos
 * del diagrama son campos y no se comparten entre usuarios.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GestorSeguimientoBolsines {

    private static final Logger log = LoggerFactory.getLogger(GestorSeguimientoBolsines.class);

    private final SesionRepository sesionRepository;
    private final BolsinRepository bolsinRepository;
    private final EmpleadoRepository empleadoRepository;

    // ---- Atributos del diagrama de analisis ----
    private Sesion actualSesion;
    private ComisionMedica cmUsuario;
    private String nombreCMUsuario;
    private final List<Bolsin> bolsinesEnviadosYDeCMOrigenUsuario = new ArrayList<>();
    private final List<Integer> numerosBolsines = new ArrayList<>();
    private final List<Integer> numerosPrecintoBolsines = new ArrayList<>();
    private final List<String> cmDestinoBolsines = new ArrayList<>();
    private Object[][] datosGPS = new Object[0][];
    private Bolsin seleccionBolsin;
    private boolean seleccionConfirmacion;
    private String emailGCMD;
    private String mensajeEjecucionCU;
    private Empleado gerenteEmpleado;

    public GestorSeguimientoBolsines(SesionRepository sesionRepository, BolsinRepository bolsinRepository,
            EmpleadoRepository empleadoRepository) {
        this.sesionRepository = sesionRepository;
        this.bolsinRepository = bolsinRepository;
        this.empleadoRepository = empleadoRepository;
    }

    // Paso 1: el EB selecciona la opcion de consultar la ubicacion de los bolsines
    public void opcionConsultarUbiBolsines() {
        buscarCMLogueado();
        buscarBolsinesEnviadosYDeCMOrigenUsuario();
        if (bolsinesEnviadosYDeCMOrigenUsuario.isEmpty()) {
            // A1: no se encuentran bolsines en estado Enviado
            mensajeEjecucionCU = "No se encuentran bolsines en estado Enviado con origen en " + nombreCMUsuario + ".";
            return;
        }
        obtenerDatosGPS();
        armarMapa();
    }

    // Paso 2: busca la CM del usuario logueado (Sesion -> Usuario -> Empleado -> CM)
    public void buscarCMLogueado() {
        actualSesion = sesionRepository
                .findFirstByFechaHoraExpiracionAfterOrderByFechaHoraInicioDesc(LocalDateTime.now())
                .orElseThrow(SinSesionActivaException::new);
        cmUsuario = actualSesion.getCMUsuario();
        nombreCMUsuario = cmUsuario.getNombre();
    }

    // Paso 3: bolsines en estado Enviado cuya CM origen es la del usuario logueado
    public void buscarBolsinesEnviadosYDeCMOrigenUsuario() {
        for (Bolsin bolsin : bolsinRepository.findAll()) {
            if (bolsin.esTuCMOrigenCMDeUsuario(cmUsuario) && bolsin.conocerCambioEstado()) {
                bolsinesEnviadosYDeCMOrigenUsuario.add(bolsin);
                numerosBolsines.add(bolsin.getNumeroBolsin());
                numerosPrecintoBolsines.add(bolsin.getNumeroPrecinto());
                cmDestinoBolsines.add(bolsin.conocerCMDestino());
            }
        }
    }

    // Pasos 4 y 5: PENDIENTE, se resuelve con InterfazGPSTracker (patron Adapter)
    public void obtenerDatosGPS() {
        datosGPS = new Object[0][];
    }

    // Paso 6: PENDIENTE, se resuelve con InterfazGoogleMaps
    public void armarMapa() {
    }

    // Paso 7: el EB selecciona un bolsin del mapa
    public void tomarSeleccionBolsin(Integer numeroBolsin) {
        if (cmUsuario == null) {
            // Pedido nuevo (gestor prototype): se vuelve a armar la lista para validar la seleccion
            buscarCMLogueado();
            buscarBolsinesEnviadosYDeCMOrigenUsuario();
        }
        seleccionBolsin = bolsinesEnviadosYDeCMOrigenUsuario.stream()
                .filter(bolsin -> bolsin.getNumeroBolsin().equals(numeroBolsin))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "El bolsín " + numeroBolsin + " no está entre los bolsines enviados de " + nombreCMUsuario + "."));
    }

    // Paso 9: el EB confirma (o no) el envio del correo al GCM destino
    public void tomarConfirmacion(boolean confirmacion) {
        seleccionConfirmacion = confirmacion;
        if (!seleccionConfirmacion) {
            // A5: el EB no selecciona la opcion para enviar un correo
            mensajeEjecucionCU = "No se envió el correo al Gerente de la CM destino.";
            finCU();
            return;
        }
        buscarMailGCMD();
        llamarACUNotificarUbicacionBolsin();
        finCU();
    }

    // Paso 10: busca el email del Gerente de la CM destino del bolsin seleccionado
    public void buscarMailGCMD() {
        String nombreCMDestino = seleccionBolsin.conocerCMDestino();
        for (Empleado empleado : empleadoRepository.findAll()) {
            Empleado gcm = empleado.sosGCM();
            if (gcm != null && gcm.esTuCMDestinoBolsin(nombreCMDestino)) {
                gerenteEmpleado = gcm;
                emailGCMD = gcm.getEmail();
                return;
            }
        }
        throw new IllegalStateException("No se encontró el Gerente de la CM destino " + nombreCMDestino + ".");
    }

    // Paso 11: include del CU 31 Notificar ubicacion de bolsin (fuera de alcance, se simula en el log)
    public void llamarACUNotificarUbicacionBolsin() {
        log.info("CU 31 Notificar ubicación de bolsín -> bolsín {} a {} ({} {})",
                seleccionBolsin.getNumeroBolsin(), emailGCMD, gerenteEmpleado.getNombre(), gerenteEmpleado.getApellido());
        mensajeEjecucionCU = "Se notificó la ubicación del bolsín " + seleccionBolsin.getNumeroBolsin()
                + " al Gerente de " + seleccionBolsin.conocerCMDestino() + " (" + emailGCMD + ").";
    }

    public void finCU() {
        log.info("Fin CU 36: {}", mensajeEjecucionCU);
    }

    // ---- Getters para que la capa web arme la respuesta a la pantalla ----

    public String getNombreCMUsuario() {
        return nombreCMUsuario;
    }

    public List<Integer> getNumerosBolsines() {
        return Collections.unmodifiableList(numerosBolsines);
    }

    public List<Integer> getNumerosPrecintoBolsines() {
        return Collections.unmodifiableList(numerosPrecintoBolsines);
    }

    public List<String> getCmDestinoBolsines() {
        return Collections.unmodifiableList(cmDestinoBolsines);
    }

    public Object[][] getDatosGPS() {
        return datosGPS;
    }

    public String getEmailGCMD() {
        return emailGCMD;
    }

    public String getMensajeEjecucionCU() {
        return mensajeEjecucionCU;
    }
}
