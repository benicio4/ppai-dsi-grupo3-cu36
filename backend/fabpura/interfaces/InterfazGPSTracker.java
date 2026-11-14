package interfaces;

import java.util.List;
import entidades.Bolsin;

public class InterfazGPSTracker {
    
    public String getBolsinLocation(Bolsin bolsin) {
        System.out.println("[GPS TRACKER] Obteniendo ubicación satelital del bolsín " + bolsin.getNumeroBolsin());
        return "-31.4167, -64.1833";
    } //no sé como hacer este

}