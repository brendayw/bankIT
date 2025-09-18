package ar.edu.utn.frbb.tup.model.cuenta.exceptions;

public class TipoCuentaYaExisteException extends RuntimeException {
    public TipoCuentaYaExisteException(String message) {
        super(message);
    }
}
