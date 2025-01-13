package ar.edu.utn.frbb.tup.model.exception.cuenta;

public class CuentaNoSoportadaException extends RuntimeException  {
    public CuentaNoSoportadaException(String message) {
        super(message);
    }
}
