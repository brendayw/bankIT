package ar.edu.utn.frbb.tup.model.exception.prestamo;

public class PrestamoNoExisteException extends RuntimeException {
    public PrestamoNoExisteException(String message) {
        super(message);
    }
}
