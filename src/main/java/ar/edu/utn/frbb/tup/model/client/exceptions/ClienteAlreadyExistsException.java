package ar.edu.utn.frbb.tup.model.client.exceptions;

public class ClienteAlreadyExistsException extends RuntimeException  {
    public ClienteAlreadyExistsException(String message) {
        super(message);
    }
}
