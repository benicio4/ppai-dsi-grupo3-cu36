package grupo3.ppai.dtos;

import java.util.ArrayList;
import java.util.List;

import grupo3.ppai.gestores.GestorSeguimientoBolsines;

/** Respuesta de opcionConsultarUbiBolsines(): CM del usuario y bolsines enviados a mostrar. */
public record ConsultaBolsinesDTO(String nombreCM, List<BolsinDTO> bolsines, String mensaje) {

    public record BolsinDTO(Integer numeroBolsin, Integer numeroPrecinto, String cmDestino) {
    }

    public static ConsultaBolsinesDTO desde(GestorSeguimientoBolsines gestor) {
        List<BolsinDTO> bolsines = new ArrayList<>();
        for (int i = 0; i < gestor.getNumerosBolsines().size(); i++) {
            bolsines.add(new BolsinDTO(
                    gestor.getNumerosBolsines().get(i),
                    gestor.getNumerosPrecintoBolsines().get(i),
                    gestor.getCmDestinoBolsines().get(i)));
        }
        return new ConsultaBolsinesDTO(gestor.getNombreCMUsuario(), bolsines, gestor.getMensajeEjecucionCU());
    }
}
