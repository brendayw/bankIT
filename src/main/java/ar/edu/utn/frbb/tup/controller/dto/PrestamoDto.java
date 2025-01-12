package ar.edu.utn.frbb.tup.controller.dto;

public class PrestamoDto {
    private long dniTitular;
    private double montoPrestamo;
    private String tipoMoneda;
    private int plazoMeses;

    //getters y setters
    public long getDniTitular() {
        return dniTitular;
    }
    public void setDniTitular(long dniTitular) {
        this.dniTitular = dniTitular;
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

}
