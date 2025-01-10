package ar.edu.utn.frbb.tup.model.exception.cliente;

public class ClienteAlreadyExistsException extends Throwable {
    public ClienteAlreadyExistsException(String message) {
        super(message);
    }
}
