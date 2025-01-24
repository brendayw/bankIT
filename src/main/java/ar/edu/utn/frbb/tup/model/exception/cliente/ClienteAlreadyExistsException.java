package ar.edu.utn.frbb.tup.model.exception.cliente;

public class ClienteAlreadyExistsException extends Exception  {
    public ClienteAlreadyExistsException(String message) {
        super(message);
    }
}
