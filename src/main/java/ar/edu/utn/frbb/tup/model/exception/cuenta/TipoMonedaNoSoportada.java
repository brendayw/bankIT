package ar.edu.utn.frbb.tup.model.exception.cuenta;

public class TipoMonedaNoSoportada extends RuntimeException {
    public TipoMonedaNoSoportada(String message) {
        super(message);
    }
}
