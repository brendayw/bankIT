package ar.edu.utn.frbb.tup.model.cliente.exceptions;

public class TipoPersonaNoSoportada extends RuntimeException  {
    public TipoPersonaNoSoportada(String message) {
        super(message);
    }
}
