package ar.edu.utn.frbb.tup.model.cuenta.exceptions;

public class CuentaYaExisteException extends RuntimeException {
    public CuentaYaExisteException(String message) {
        super(message);
    }
}
