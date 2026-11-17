package grupo3.ppai.gestores;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import grupo3.ppai.entidades.Bolsin;
import grupo3.ppai.entidades.ComisionMedica;
import grupo3.ppai.entidades.Empleado;
import grupo3.ppai.entidades.Sesion;
import grupo3.ppai.externos.adaptadores.AdaptadorGeoPulseMTR_900;
import grupo3.ppai.externos.adaptadores.AdaptadorNavTrackQX_7A;
import grupo3.ppai.externos.adaptadores.AdaptadorXTR_4500L;
import grupo3.ppai.externos.adaptadores.IAdaptadorCoordenadasBolsines;
import grupo3.ppai.externos.servicios.ModeloGPS;
import grupo3.ppai.repositorios.BolsinRepository;
import grupo3.ppai.repositorios.EmpleadoRepository;
import grupo3.ppai.repositorios.SesionRepository;
import lombok.Getter;

/**
 * <<control>> GestorSeguimientoBolsines - CU 36 Consultar seguimiento de bolsines.
 *
 * Una instancia nueva por pedido (prototype, ver docs/DECISIONES.md): los atributos
 * del diagrama son campos y no se comparten entre usuarios.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Getter
public class GestorSeguimientoBolsines {

    private static final Logger log = LoggerFactory.getLogger(GestorSeguimientoBolsines.class);

    private final SesionRepository sesionRepository;
    private final BolsinRepository bolsinRepository;
    private final EmpleadoRepository empleadoRepository;

    // U.P: "Absoluta honestidad, esto no lo podria haber hecho sin claude, es un nivel muy avanzado de springboot"
    // Los adaptadores son prototype: ObjectProvider.getObject() da una instancia nueva cada vez,
    // con su servicio y su API key ya inyectados por Spring.
    private final ObjectProvider<AdaptadorXTR_4500L> adaptadorXTR;
    private final ObjectProvider<AdaptadorNavTrackQX_7A> adaptadorNavTrack;
    private final ObjectProvider<AdaptadorGeoPulseMTR_900> adaptadorGeoPulse;

    // ---- Atributos del diagrama de analisis ----
    private Sesion actualSesion;
    private ComisionMedica cmUsuario;
    private String nombreCMUsuario;
    private final List<Bolsin> bolsinesEnviadosYDeCMOrigenUsuario = new ArrayList<>();
    private final List<Integer> numerosBolsines = new ArrayList<>();
    private final List<Integer> numerosPrecintoBolsines = new ArrayList<>();
    private final List<String> cmDestinoBolsines = new ArrayList<>();
    private List<ArrayList<Object>> datosGPS;
    private Bolsin seleccionBolsin;
    private boolean seleccionConfirmacion;
    private String emailGCMD;
    private String mensajeEjecucionCU;
    private Empleado gerenteEmpleado;

    public GestorSeguimientoBolsines(SesionRepository sesionRepository, BolsinRepository bolsinRepository,
            EmpleadoRepository empleadoRepository,
            ObjectProvider<AdaptadorXTR_4500L> adaptadorXTR,
            ObjectProvider<AdaptadorNavTrackQX_7A> adaptadorNavTrack,
            ObjectProvider<AdaptadorGeoPulseMTR_900> adaptadorGeoPulse) {
        this.sesionRepository = sesionRepository;
        this.bolsinRepository = bolsinRepository;
        this.empleadoRepository = empleadoRepository;
        this.adaptadorXTR = adaptadorXTR;
        this.adaptadorNavTrack = adaptadorNavTrack;
        this.adaptadorGeoPulse = adaptadorGeoPulse;
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

    // Pasos 4: solicita al dispositivo GPS Tracker los datos de localización de los bolsines
    public void obtenerDatosGPS() {
        // hardcodeamos la creacion de un adapter para el gps XTR-4500L
        IAdaptadorCoordenadasBolsines adapter = crearAdapter(ModeloGPS.XTR_4500L);

        this.datosGPS = solicitarCoordenadasBolsines(adapter);

    }

    // Crea el adapter segun uno de los 3 modelos disponibles en el enum
    public IAdaptadorCoordenadasBolsines crearAdapter(ModeloGPS modelo) {
        return switch (modelo) {
            case XTR_4500L        -> adaptadorXTR.getObject();
            case NAVTRACK_QX_7A   -> adaptadorNavTrack.getObject();
            case GEOPULSE_MTR_900 -> adaptadorGeoPulse.getObject();
        };
    }

    // Paso 5: recibe del dispositivo GPS Tracker los datos de localización de cada uno de esos bolsines
    public List<ArrayList<Object>> solicitarCoordenadasBolsines(IAdaptadorCoordenadasBolsines adapter) {

        // Obtengo los codigos de las CMDestino, aplicando Program to Interface Principle
        // Esto se hace así en caso de que el modelo utilizado sea el navtracker, ya que este método no le importa cual adapter posta se esta usando
        // (estoy re orgulloso de que me haya salido un stream)
        String[] codigosCMDestino = bolsinesEnviadosYDeCMOrigenUsuario.stream().map(Bolsin::getDestino).map(ComisionMedica::getCodigo).toArray(String[]::new);

        return adapter.recibirDatosGPS(numerosBolsines, cmUsuario.getCodigo(), codigosCMDestino);
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
}
