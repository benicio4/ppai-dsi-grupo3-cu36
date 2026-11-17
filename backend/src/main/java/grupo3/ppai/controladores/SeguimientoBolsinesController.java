package grupo3.ppai.controladores;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import grupo3.ppai.dtos.ConfirmacionDTO;
import grupo3.ppai.dtos.ConsultaBolsinesDTO;
import grupo3.ppai.dtos.MensajeDTO;
import grupo3.ppai.gestores.GestorSeguimientoBolsines;
import grupo3.ppai.gestores.SinSesionActivaException;

/**
 * Puente HTTP entre PantSeguimientoBolsines (React) y GestorSeguimientoBolsines.
 * No tiene logica del CU: solo pide un gestor nuevo y le delega.
 */
@RestController
@RequestMapping("/api/seguimiento-bolsines")
public class SeguimientoBolsinesController {

    private final ObjectProvider<GestorSeguimientoBolsines> gestores;

    public SeguimientoBolsinesController(ObjectProvider<GestorSeguimientoBolsines> gestores) {
        this.gestores = gestores;
    }

    @PostMapping("/consulta")
    public ConsultaBolsinesDTO opcionConsultarUbiBolsines() {
        GestorSeguimientoBolsines gestor = gestores.getObject();
        gestor.opcionConsultarUbiBolsines();
        return ConsultaBolsinesDTO.desde(gestor);
    }

    @PostMapping("/confirmacion")
    public MensajeDTO tomarConfirmacion(@RequestBody ConfirmacionDTO pedido) {
        GestorSeguimientoBolsines gestor = gestores.getObject();
        gestor.tomarSeleccionBolsin(pedido.numeroBolsin());
        gestor.tomarConfirmacion(pedido.confirmacion());
        return new MensajeDTO(gestor.getMensajeEjecucionCU());
    }

    @ExceptionHandler(SinSesionActivaException.class)
    public ResponseEntity<MensajeDTO> sinSesion(SinSesionActivaException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MensajeDTO(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MensajeDTO> noEncontrado(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeDTO(e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<MensajeDTO> conflicto(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MensajeDTO(e.getMessage()));
    }
}
