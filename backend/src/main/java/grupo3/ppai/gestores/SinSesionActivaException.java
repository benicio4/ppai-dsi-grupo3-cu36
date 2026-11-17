package grupo3.ppai.gestores;

public class SinSesionActivaException extends RuntimeException {

    public SinSesionActivaException() {
        super("No hay una sesión activa. Inicie sesión para continuar.");
    }
}
