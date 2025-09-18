package ar.edu.utn.frbb.tup.model.loan.exceptions;

public class PrestamoNoExisteException extends RuntimeException {
    public PrestamoNoExisteException(String message) {
        super(message);
    }
}
