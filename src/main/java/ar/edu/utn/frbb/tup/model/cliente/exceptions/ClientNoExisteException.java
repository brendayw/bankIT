package ar.edu.utn.frbb.tup.model.cliente.exceptions;

public class ClientNoExisteException extends RuntimeException {
    public ClientNoExisteException(String message) {
        super(message);
    }
}

