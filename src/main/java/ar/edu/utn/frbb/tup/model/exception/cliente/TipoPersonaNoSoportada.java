package ar.edu.utn.frbb.tup.model.exception.cliente;

public class TipoPersonaNoSoportada extends RuntimeException  {
    public TipoPersonaNoSoportada(String message) {
        super(message);
    }
}
