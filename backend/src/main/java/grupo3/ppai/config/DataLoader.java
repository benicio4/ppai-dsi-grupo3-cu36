package grupo3.ppai.config;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import grupo3.ppai.entidades.Bolsin;
import grupo3.ppai.entidades.CambioEstadoBolsin;
import grupo3.ppai.entidades.ComisionMedica;
import grupo3.ppai.entidades.Empleado;
import grupo3.ppai.entidades.Estado;
import grupo3.ppai.entidades.Rol;
import grupo3.ppai.entidades.Sesion;
import grupo3.ppai.entidades.Usuario;
import grupo3.ppai.repositorios.BolsinRepository;
import grupo3.ppai.repositorios.ComisionMedicaRepository;
import grupo3.ppai.repositorios.EmpleadoRepository;
import grupo3.ppai.repositorios.EstadoRepository;
import grupo3.ppai.repositorios.RolRepository;
import grupo3.ppai.repositorios.SesionRepository;
import grupo3.ppai.repositorios.UsuarioRepository;

/**
 * Carga datos de prueba si la base esta vacia y asegura que haya una sesion vigente
 * (mientras no exista el login, el Gestor toma la sesion vigente mas reciente).
 */
@Component
public class DataLoader implements CommandLineRunner {

    private static final String NOMBRE_USUARIO_EB = "encargado.cba";
    private static final String CORREO_COMISION_DEMO = "cm.cordoba@correo.test";
    private static final String CODIGO_ORIGEN_DEMO = "CM-CBA-02";

    private final RolRepository rolRepository;
    private final ComisionMedicaRepository comisionMedicaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SesionRepository sesionRepository;
    private final EstadoRepository estadoRepository;
    private final BolsinRepository bolsinRepository;

    public DataLoader(RolRepository rolRepository, ComisionMedicaRepository comisionMedicaRepository,
            EmpleadoRepository empleadoRepository, UsuarioRepository usuarioRepository,
            SesionRepository sesionRepository, EstadoRepository estadoRepository, BolsinRepository bolsinRepository) {
        this.rolRepository = rolRepository;
        this.comisionMedicaRepository = comisionMedicaRepository;
        this.empleadoRepository = empleadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.sesionRepository = sesionRepository;
        this.estadoRepository = estadoRepository;
        this.bolsinRepository = bolsinRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            cargarDatos();
        } else {
            actualizarCodigoOrigenDemo();
        }
        asegurarSesionVigente();
    }

    private void cargarDatos() {
        Rol rolEB = rolRepository.save(new Rol("Encargado de Bolsines", "EB"));
        Rol rolGCM = rolRepository.save(new Rol("Gerente de Comisión Médica", "GCM"));

        ComisionMedica cba = comisionMedicaRepository.save(new ComisionMedica(
                CODIGO_ORIGEN_DEMO, "Av. Colón 1234, Córdoba", CORREO_COMISION_DEMO, "3514000001", "CM Córdoba"));
        ComisionMedica ros = comisionMedicaRepository.save(new ComisionMedica(
                "CM-ROS-04", "Bv. Oroño 500, Rosario", "cm.rosario@correo.test", "3414000002", "CM Rosario"));
        ComisionMedica mza = comisionMedicaRepository.save(new ComisionMedica(
                "CM-MDZ-03", "San Martín 800, Mendoza", "cm.mendoza@correo.test", "2614000003", "CM Mendoza"));
        ComisionMedica cat = comisionMedicaRepository.save(new ComisionMedica(
                "CM-CAT-05", "Sarmiento 650, San Fernando del Valle de Catamarca", "cm.catamarca@correo.test",
                "3834000004", "CM Catamarca"));
        ComisionMedica bsas = comisionMedicaRepository.save(new ComisionMedica(
                "CM-BA-01", "Av. de Mayo 1200, Ciudad Autónoma de Buenos Aires", "cm.buenosaires@correo.test", "1140000005",
                "CM Buenos Aires"));

        Empleado eb = empleadoRepository.save(new Empleado("Pérez", "Juan", "juan.perez@correo.test", rolEB, cba));
        empleadoRepository.save(new Empleado("Gómez", "Laura", "gcm.cordoba@correo.test", rolGCM, cba));
        empleadoRepository.save(new Empleado("Díaz", "Martín", "gcm.rosario@correo.test", rolGCM, ros));
        empleadoRepository.save(new Empleado("Sosa", "Carla", "gcm.mendoza@correo.test", rolGCM, mza));
        empleadoRepository.save(new Empleado("Herrera", "Sofía", "gcm.catamarca@correo.test", rolGCM, cat));
        empleadoRepository.save(new Empleado("Fernández", "Diego", "gcm.buenosaires@correo.test", rolGCM, bsas));

        String hash = new BCryptPasswordEncoder().encode("1234");
        usuarioRepository.save(new Usuario(NOMBRE_USUARIO_EB, hash, eb));

        Estado creado = estadoRepository.save(new Estado("Creado", "Bolsín armado en la CM origen", "Bolsin"));
        Estado enviado = estadoRepository.save(new Estado("Enviado", "Bolsín en viaje a la CM destino", "Bolsin"));
        Estado recibido = estadoRepository.save(new Estado("Recibido", "Bolsín recibido en la CM destino", "Bolsin"));

        LocalDateTime hace2Dias = LocalDateTime.now().minusDays(2);
        LocalDateTime ayer = LocalDateTime.now().minusDays(1);

        LocalDateTime hoy = LocalDateTime.now();

        // Numeros de bolsin del 101 al 110: son los que la API mock de GPS reconoce
        // Enviados desde Cordoba: los que tiene que ver el EB logueado
        crearBolsin(101, 5001, cba, ros, eb, new Estado[] { creado, enviado }, hace2Dias, ayer);
        crearBolsin(102, 5002, cba, mza, eb, new Estado[] { creado, enviado }, hace2Dias, ayer);
        crearBolsin(104, 5004, cba, ros, eb, new Estado[] { creado, enviado }, hace2Dias, ayer);
        crearBolsin(105, 5005, cba, cat, eb, new Estado[] { creado, enviado }, hace2Dias, ayer);
        crearBolsin(106, 5006, cba, bsas, eb, new Estado[] { creado, enviado }, hace2Dias, ayer);
        crearBolsin(107, 5007, cba, bsas, eb, new Estado[] { creado, enviado }, hace2Dias, ayer);
        // No deben aparecer: uno todavia Creado, uno ya Recibido, dos con otra CM origen
        crearBolsin(108, 5008, cba, mza, eb, new Estado[] { creado }, hace2Dias);
        crearBolsin(109, 5009, cba, ros, eb, new Estado[] { creado, enviado, recibido }, hace2Dias, ayer, hoy);
        crearBolsin(103, 5003, mza, bsas, eb, new Estado[] { creado, enviado }, hace2Dias, ayer);
        crearBolsin(110, 5010, ros, cba, eb, new Estado[] { creado, enviado }, hace2Dias, ayer);
    }

    /** Arma el historial: cada cambio se cierra cuando empieza el siguiente; el ultimo queda actual. */
    private void crearBolsin(int numero, int precinto, ComisionMedica origen, ComisionMedica destino,
            Empleado responsable, Estado[] estados, LocalDateTime... fechas) {
        Bolsin bolsin = new Bolsin(numero, precinto, 2.5, fechas[0].toLocalDate(), LocalTime.of(10, 0), origen, destino);
        CambioEstadoBolsin anterior = null;
        for (int i = 0; i < estados.length; i++) {
            if (anterior != null) {
                anterior.setFechaFin(fechas[i]);
            }
            anterior = new CambioEstadoBolsin(fechas[i], estados[i], responsable);
            bolsin.agregarCambioEstado(anterior);
        }
        bolsinRepository.save(bolsin);
    }

    // Conserva la base H2 existente y alinea la comisión de prueba con el bolsín 101 del GPS.
    private void actualizarCodigoOrigenDemo() {
        usuarioRepository.findAll().stream()
                .filter(usuario -> NOMBRE_USUARIO_EB.equals(usuario.getNombre()))
                .map(Usuario::getCMEmpleado)
                .filter(cm -> "CM-CBA-02".equals(cm.getCodigo()) && CORREO_COMISION_DEMO.equals(cm.getEmail()))
                .findFirst()
                .ifPresent(cm -> cm.setCodigo(CODIGO_ORIGEN_DEMO));
    }

    private void asegurarSesionVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        if (sesionRepository.findFirstByFechaHoraExpiracionAfterOrderByFechaHoraInicioDesc(ahora).isPresent()) {
            return;
        }
        usuarioRepository.findAll().stream()
                .filter(u -> NOMBRE_USUARIO_EB.equals(u.getNombre()))
                .findFirst()
                .ifPresent(u -> sesionRepository.save(new Sesion(ahora, ahora.plusHours(8), u)));
    }
}
