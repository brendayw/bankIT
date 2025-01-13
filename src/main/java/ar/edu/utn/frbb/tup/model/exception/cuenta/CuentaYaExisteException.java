package ar.edu.utn.frbb.tup.model.exception.cuenta;

public class CuentaYaExisteException extends RuntimeException {
    public CuentaYaExisteException(String message) {
        super(message);
    }
}
