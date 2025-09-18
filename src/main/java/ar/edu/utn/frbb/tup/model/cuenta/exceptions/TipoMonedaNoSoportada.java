package ar.edu.utn.frbb.tup.model.cuenta.exceptions;

public class TipoMonedaNoSoportada extends RuntimeException {
    public TipoMonedaNoSoportada(String message) {
        super(message);
    }
}
