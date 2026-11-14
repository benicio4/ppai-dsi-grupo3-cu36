package pantalla;

import java.util.List;
import control.GestorSeguimientoBolsines;
import entidades.Bolsin;

public class PantSeguimientoBolsines {
    private Object botonConfirmar;
    private Object botonCancelar;
    private Object botonEnviarMail;
    private String txtNumBolsin;
    private String labelNumPrecinto;
    private String txtCMDestino;
    private String labelCMDestino;
    private String infoCMLogueado;
    private Object comboSeleccionBolsin;
    private boolean checkboxFiltro;
    private Object mapaBolsines;

    private GestorSeguimientoBolsines gestor;

    public PantSeguimientoBolsines(GestorSeguimientoBolsines gestor) {
        this.gestor = gestor;
    }

    public void opcionConsultarUbiBolsines() {
        habilitarPantalla();
        gestor.opcionConsultarUbiBolsines();
    }
    
    public void habilitarPantalla() {
        System.out.println("[Pantalla] Pantalla de Seguimiento de Bolsines habilitada");
    }

    public void mostrarNombreCM(String nombreCM) {
        System.out.println("[Pantalla] Nombre de la Comisión Médica logueada: " + nombreCM);
    }

    public void mostrarMapasBolsines(Object mapa, List<String> bolsines, List<String> precintos, List<Stirng> destinos) {
        this.mapaBolsines = mapa;
        System.out.println("[Pantalla] Mapa de ubicación de los bolsines: " + bolsines);
    }

    public void habilitarOpcionDeSeleccion(){
        System.out.println("[Pantalla] Opción de selección de bolsines habilitada");
    }

    public void habilitarOpcionesDeFiltrado(){
        System.out.println("[Pantalla] Opciones de filtrado habilitadas");
    }

    public void tomarSeleccionBolsin(Bolsin bolsin){
        gestor.tomarSeleccionBolsin(bolsin);
    }

    public void consultarEnvioMailGMC(){
        System.out.println("[Pantalla] Consultando confirmación para el envío de mail al GMC");
    }

    public void tomarSeleccionConfirmacion(boolean confirmacion){
        gestor.tomarConfirmacion(confirmacion);
    }

    public void mostrarMensajeEjecucionExitosa(String mensaje) {
        System.out.println("[Pantalla] " + mensaje);
    }
    
}
