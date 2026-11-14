package control;

import java.util.List;
import java.util.ArrayList;


import entidades.Bolsin;
import entidades.ComisionMedica;
import entidades.Empleado;
//import entidades.Sesion;

import pantalla.PantSeguimientoBolsines;
import interfaces.InterfazGPSTracker;
import interfaces.InterfazGoogleMaps;

public class gestorSeguimientoBolsines {
    private Sesion actualSesion;
    private ComisionMedica cmUsuario;
    private String nombreCUsuario;
    private List<Bolsin> bolsinesEnviadosYdeCMOrigenUsuario;
    private List<String> numerosBolsines;
    private List<String> numerosPrecintosBolsines;
    private List<String> cmDestinoBolsines;
    private List<String> datosGPS;
    private Bolsin seleccionBolsin;
    private boolean seleccionConfirmacion;
    private String emailGCMD;
    private String mensajeEjecucionCU;
    private Empleado gerenteEmpleado;


    private PantSeguimientoBolsines pantalla;
    private InterfazGPSTracker interfazGPS;
    private InterfazGoogleMaps interfazMaps;

    private List<Bolsin> bolsines;
   private List<Empleado> empleados;

    public gestorSeguimientoBolsines(Sesion actualSesion, List<Bolsin> bolsines, List<Empleado> empleados) {
        this.actualSesion = actualSesion;
        this.bolsines = bolsines;
        this.empleados = empleados;
        this.bolsinesEnviadosYdeCMOrigenUsuario = new ArrayList<>();
        this.numerosBolsines = new ArrayList<>();
        this.numerosPrecintosBolsines = new ArrayList<>();
        this.cmDestinoBolsines = new ArrayList<>();
        this.datosGPS = new ArrayList<>();
        this.interfazGPS = new InterfazGPSTracker();
        this.interfazMaps = new InterfazGoogleMaps();
    }

    public void setPantalla(PantSeguimientoBolsines pantalla) {
        this.pantalla = pantalla;
    }

    public void opcionConsultarUbiBolsines() {
        buscarCMLogueado();
        pantalla.mostrarNombreCM(nombreCMUsuario);
        buscarBolsinesEnviadosYDeCMOrigenUsuario();
        obtenerDatosGPS();
        armarMapa();
    }

    public void buscarCMLogueado() {
        // actualSesion.getCMUsuario() -> Usuario.getCMEmpleado() -> Empleado.getCM() -> CM.getNombre()
        this.cmUsuario = actualSesion.getCMUsuario();
        this.nombreCMUsuario = cmUsuario.getNombre();
    }

     public void buscarBolsinesEnviadosYDeCMOrigenUsuario() {
        // Loop: [Mientras haya bolsines] 
        for (Bolsin bolsin : bolsines) {
            // bolsin.sosEnviado() -> cambioEstado.sosActual() -> estado.esEnviado()
            if (bolsin.sosEnviado() && bolsin.esTuCMOrigenCMDeUsuario(cmUsuario)) {
                bolsinesEnviadosYdeCMOrigenUsuario.add(bolsin);
                numerosBolsines.add(bolsin.getNumeroBolsin());
                numerosPrecintoBolsines.add(bolsin.getNumeroPrecinto());
                
                // bolsin.conocerCMDestino() -> cmDestino.getNombre()
                ComisionMedica cmDestino = bolsin.conocerCMDestino();
                cmDestinoBolsines.add(cmDestino.getNombre());
            }
        }
    }

    public void obtenerDatosGPS() {
        // * getBolsinLocation() por cada bolsín filtrado
        for (Bolsin enviado : bolsinesEnviadosYdeCMOrigenUsuario) {
            String coordenadas = interfazGPS.getBolsinLocation(enviado);
            datosGPS.add(coordenadas);
        }
    }

    public void armarMapa() {
        // construirMapaConUbicacionesBolsines() y pantalla para mostrar y filtrar los datos obtenidos
        Object mapa = interfazMaps.construirMapaConUbicacionesBolsines(datosGPS);
        pantalla.mostrarMapaBolsines(mapa, numerosBolsines, numerosPrecintoBolsines, cmDestinoBolsines);
        pantalla.habilitarOpcionDeSelecion();
        pantalla.habilitarOpcionesDeFiltrado();
    }

    public void tomarSeleccionBolsin(Bolsin bolsinSeleccionado) {
        this.seleccionBolsin = bolsinSeleccionado;
        pantalla.consultarEnvioMailGCM();
    }

    public void tomarConfirmacion(boolean confirmacion) {
        this.seleccionConfirmacion = confirmacion;
        if (this.seleccionConfirmacion) {
            buscarMailGCMD();
            llamarACUNotificarUbicacionBolsin();
            pantalla.mostrarMensajeEjecucionExitosa(mensajeEjecucionCU);
        }
        finCU();
    }

    public void buscarMailGCMD() {
        ComisionMedica cmDestino = seleccionBolsin.conocerCMDestino();
        
        // Loop: [Hasta encontrar al Gerente de la CM Destino] 
        for (Empleado emp : empleados) {
            // emp.esTuCMCMDestinoBolsin() y emp.sosGCM() -> rol.esGCM()
            if (emp.esTuCMCMDestinoBolsin(cmDestino) && emp.sosGCM()) {
                this.gerenteEmpleado = emp;
                this.emailGCMD = emp.getEmail();
                break; 
            }
        }
    }

    public void llamarACUNotificarUbicacionBolsin() {
        // <<includes>> Caso de Uso 31: Notificar ubicación de Bolsín
        System.out.println("[CU 31 - Notificar ubicación de Bolsín] Enviando notificación a: " + emailGCMD);
        this.mensajeEjecucionCU = "Se notificó con éxito la ubicación del bolsín al mail: " + emailGCMD;
    }

    public void finCU() {
        System.out.println("[Gestor] Fin del Caso de Uso.");
    }

}
