package ar.edu.utn.frbb.tup.model.prestamo.exceptions;

public class PrestamoNoExisteException extends RuntimeException {
    public PrestamoNoExisteException(String message) {
        super(message);
    }
}
