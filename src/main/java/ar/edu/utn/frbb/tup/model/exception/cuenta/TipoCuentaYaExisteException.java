package ar.edu.utn.frbb.tup.model.exception.cuenta;

public class TipoCuentaYaExisteException extends RuntimeException {
    public TipoCuentaYaExisteException(String message) {
        super(message);
    }
}
