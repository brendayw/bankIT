package ar.edu.utn.frbb.tup.model.exception.cuenta;

public class CuentaNoExisteException extends RuntimeException {
    public CuentaNoExisteException(String message) {
        super(message);
    }

}
