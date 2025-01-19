package ar.edu.utn.frbb.tup.model;

import java.util.List;

public class PrestamoRespuesta {
    private long numeroCliente;
    private List<PrestamoDetalle> prestamos;

    public PrestamoRespuesta(long numeroCliente, List<PrestamoDetalle> prestamos) {
        this.numeroCliente = numeroCliente;
        this.prestamos = prestamos;
    }

    public long getNumeroCliente() {
        return numeroCliente;
    }
    public List<PrestamoDetalle> getPrestamos() {
        return prestamos;
    }
}
