package ar.edu.utn.frbb.tup.model.account.exceptions;

public class TipoCuentaYaExisteException extends RuntimeException {
    public TipoCuentaYaExisteException(String message) {
        super(message);
    }
}
