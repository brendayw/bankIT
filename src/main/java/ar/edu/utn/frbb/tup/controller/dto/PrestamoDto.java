package ar.edu.utn.frbb.tup.controller.dto;

public class PrestamoDto {
    private long numeroCliente;
    private double montoPrestamo;
    private String tipoMoneda;
    private int plazoMeses;

    //getters y setters
    public long getNumeroCliente() {
        return numeroCliente;
    }
    public void setNumeroCliente(long numeroCliente) {
        this.numeroCliente = numeroCliente;
    }

    public Integer getPlazoMeses() {
        return plazoMeses;
    }
    public void setPlazoMeses(Integer plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public Double getMontoPrestamo() {
        return montoPrestamo;
    }
    public void setMontoPrestamo(Double montoPrestamo) {
        this.montoPrestamo = montoPrestamo;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }
    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    @Override
    public String toString() {
        return "\nPrestamoDto{" +
                "\nnumeroCliente=" + numeroCliente +
                "\nmontoPrestamo=" + montoPrestamo +
                "\nplazoMeses=" + plazoMeses +
                "\ntipoMoneda='" + tipoMoneda;
    }
}
