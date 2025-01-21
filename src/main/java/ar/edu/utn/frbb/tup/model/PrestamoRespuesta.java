package ar.edu.utn.frbb.tup.model;

import java.util.List;

public class PrestamoRespuesta {
    private long numeroCliente;
    private List<PrestamoResume> prestamos;

    public PrestamoRespuesta() {

    }

    public PrestamoRespuesta(long numeroCliente, List<PrestamoResume> prestamos) {
        this.numeroCliente = numeroCliente;
        this.prestamos = prestamos;
    }

    public long getNumeroCliente() {
        return numeroCliente;
    }
    public void setNumeroCliente(long numeroCliente) {
        this.numeroCliente = numeroCliente;
    }
    public List<PrestamoResume> getPrestamoResume() {
        return prestamos;
    }
    public void setPrestamoResume(List<PrestamoResume> prestamos) {
        this.prestamos = prestamos;
    }
}
