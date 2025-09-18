package ar.edu.utn.frbb.tup.model.account.exceptions;

public class CuentaNoExisteException extends RuntimeException {
    public CuentaNoExisteException(String message) {
        super(message);
    }

}
