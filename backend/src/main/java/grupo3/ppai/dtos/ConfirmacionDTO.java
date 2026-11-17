package grupo3.ppai.dtos;

/** Pedido de la pantalla: bolsin seleccionado y si el EB confirma el envio del correo. */
public record ConfirmacionDTO(Integer numeroBolsin, boolean confirmacion) {
}
