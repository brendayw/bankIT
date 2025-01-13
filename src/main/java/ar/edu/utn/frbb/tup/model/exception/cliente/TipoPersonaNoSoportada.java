package ar.edu.utn.frbb.tup.model.exception.cliente;

import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoMonedaNoSoportada;

public class TipoPersonaNoSoportada extends Throwable {
    public TipoPersonaNoSoportada(String message) {
        super(message);
    }
}
