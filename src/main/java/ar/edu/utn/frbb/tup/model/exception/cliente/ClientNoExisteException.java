package ar.edu.utn.frbb.tup.model.exception.cliente;

public class ClientNoExisteException extends RuntimeException {
    public ClientNoExisteException(String message) {
        super(message);
    }
}

