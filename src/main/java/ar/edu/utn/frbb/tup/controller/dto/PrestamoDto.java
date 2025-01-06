package ar.edu.utn.frbb.tup.controller.dto;

import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

public class PrestamoDto {
    private long numeroCliente;
    private int plazoMeses;
    private double montoPrestamo;
    private String moneda;

    //constructores
    public PrestamoDto() {

    }

    public PrestamoDto(long numeroCliente, int plazoMeses, double montoPrestamo, String moneda) {
        this.numeroCliente = numeroCliente;
        this.plazoMeses = plazoMeses;
        this.montoPrestamo = montoPrestamo;
        this.moneda = moneda;
    }

    public PrestamoDto(Prestamo prestamo) {
        this.numeroCliente = prestamo.getNumeroCliente().getDni();
        this.plazoMeses = prestamo.getTermMonths();
        this.montoPrestamo = prestamo.getAmount();
        this.moneda = prestamo.getMoneda().toString();
    }

    //getters y setters
    public Long getNumeroCliente() {
        return numeroCliente;
    }

    public void setNumeroCliente(Long numeroCliente) {
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

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
}
