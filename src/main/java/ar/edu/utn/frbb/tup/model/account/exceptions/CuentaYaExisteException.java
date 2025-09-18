package ar.edu.utn.frbb.tup.model.account.exceptions;

public class CuentaYaExisteException extends RuntimeException {
    public CuentaYaExisteException(String message) {
        super(message);
    }
}
