package ar.edu.utn.frbb.tup.model.client.exceptions;

public class ClientAlreadyExistsException extends RuntimeException  {
    public ClientAlreadyExistsException(String message) {
        super(message);
    }
}
