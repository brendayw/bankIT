package ar.edu.utn.frbb.tup.model;

public class PrestamoResume {
    private double monto;
    private int plazoMeses;
    private int pagosRealizados;
    private double saldoRestante;

    //constructor
    public PrestamoResume() {

    }

    public PrestamoResume(double monto, int plazoMeses, int pagosRealizados, double saldoRestante) {
        this.monto = monto;
        this.plazoMeses = plazoMeses;
        this.pagosRealizados = pagosRealizados;
        this.saldoRestante = saldoRestante;
    }

    //getters y setters
    public double getMonto() {
        return monto;
    }
    public void setMonto(double monto) {
        this.monto = monto;
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

    public String toString() {
        return "PrestamoResume: " +
                "\n monto: " + monto +
                "\n plazo meses: " + plazoMeses +
                "\n pagosrealizados: " + pagosRealizados +
                "\n saldorestante: " + saldoRestante;
    }
}
