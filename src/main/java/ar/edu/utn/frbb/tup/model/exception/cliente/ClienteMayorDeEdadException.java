package ar.edu.utn.frbb.tup.model.exception.cliente;

public class ClienteMayorDeEdadException extends RuntimeException {
    public ClienteMayorDeEdadException(String message) {
        super(message);
    }
}
