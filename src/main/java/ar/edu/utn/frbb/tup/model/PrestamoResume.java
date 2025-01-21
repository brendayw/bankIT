package ar.edu.utn.frbb.tup.model;

public class PrestamoResume {
    private double montoConInteres;
    private int plazoMeses;
    private int pagosRealizados;
    private double saldoRestante;

    //constructor
    public PrestamoResume() {

    }
    public PrestamoResume(double montoConInteres, int plazoMeses, int pagosRealizados, double saldoRestante) {
        this.montoConInteres = montoConInteres;
        this.plazoMeses = plazoMeses;
        this.pagosRealizados = pagosRealizados;
        this.saldoRestante = saldoRestante;
    }

    //getters y setters
    public double getMontoConInteres() {
        return montoConInteres;
    }
    public void setMontoConInteres(double montoConInteres) {
        this.montoConInteres = montoConInteres;
    }

    public int getPlazoMeses() {
        return plazoMeses;
    }
    public void setPlazoMeses(int plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public int getPagosRealizados() {
        return pagosRealizados;
    }
    public void setPagosRealizados(int pagosRealizados) {
        this.pagosRealizados = pagosRealizados;
    }

    public double getSaldoRestante() {
        return saldoRestante;
    }
    public void setSaldoRestante(double saldoRestante) {
        this.saldoRestante = saldoRestante;
    }
}
