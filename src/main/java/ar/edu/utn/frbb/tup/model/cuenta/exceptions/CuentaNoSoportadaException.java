package ar.edu.utn.frbb.tup.model.cuenta.exceptions;

public class CuentaNoSoportadaException extends RuntimeException  {
    public CuentaNoSoportadaException(String message) {
        super(message);
    }
}
