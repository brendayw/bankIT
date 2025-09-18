package ar.edu.utn.frbb.tup.model.account.exceptions;

public class TipoMonedaNoSoportada extends RuntimeException {
    public TipoMonedaNoSoportada(String message) {
        super(message);
    }
}
