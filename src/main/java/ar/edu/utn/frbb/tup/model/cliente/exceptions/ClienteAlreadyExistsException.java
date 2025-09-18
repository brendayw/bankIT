package ar.edu.utn.frbb.tup.model.cliente.exceptions;

public class ClienteAlreadyExistsException extends RuntimeException  {
    public ClienteAlreadyExistsException(String message) {
        super(message);
    }
}
