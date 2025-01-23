package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Prestamo {
    private long id;
    private long dniTitular;
    private double montoSolicitado;
    private double monto; //monto con interes calculado
    private TipoMoneda moneda;
    private int plazoMeses;
    private LoanStatus loanStatus;
    private String mensaje;
    private List<PlanPago> planDePagos;

    //nuevos
    private double saldoRestante;
    private int pagosRealizados;
    private final double tasaInteres;

    //constructores
    public Prestamo() {
        this.id = generarIdAleatorio();
        this.planDePagos = new ArrayList<>();
        this.tasaInteres = 0.40;
    }

    public Prestamo(PrestamoDto prestamoDto, int score) {
        this.id = generarIdAleatorio();
        this.dniTitular = prestamoDto.getNumeroCliente();
        this.montoSolicitado = prestamoDto.getMontoPrestamo();
        this.monto = prestamoDto.getMontoPrestamo();
        this.tasaInteres = 0.40;
        this.moneda = TipoMoneda.fromString(prestamoDto.getTipoMoneda());
        this.plazoMeses = prestamoDto.getPlazoMeses();
        this.loanStatus = score >= 700 ? LoanStatus.APROBADO : LoanStatus.RECHAZADO;
        this.mensaje = devolverMensaje(this.loanStatus);
    }

    public Prestamo(long id, double monto, int plazoMeses, List<PlanPago> planDePagos) {
        this.id = id;
        this.monto = monto;
        this.tasaInteres = 0.40;
        this.plazoMeses = plazoMeses;
        this.planDePagos = planDePagos;
    }

    //getters & setters
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getDniTitular() {
        return dniTitular;
    }
    public void setDniTitular(long dniTitular) {
        this.dniTitular = dniTitular;
    }

    public double getMontoSolicitado() {
        return montoSolicitado;
    }
    public void setMontoSolicitado(double montoSolicitado) {
        this.montoSolicitado = montoSolicitado;
    }

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

    public LoanStatus getLoanStatus() {
        return loanStatus;
    }
    public void setLoanStatus(LoanStatus loanStatus) {
        this.loanStatus = loanStatus;
        this.mensaje = devolverMensaje(loanStatus);
    }

    public TipoMoneda getMoneda() {
        return moneda;
    }
    public void setMoneda(TipoMoneda moneda) {
        this.moneda = moneda;
    }

    public List<PlanPago> getPlanDePagos() {
        return planDePagos;
    }
    public void setPlanDePagos(List<PlanPago> planDePagos) {
        this.planDePagos = planDePagos;
    }

    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public double getSaldoRestante() {
        return saldoRestante;
    }
    public void setSaldoRestante(double saldoRestante) {
        this.saldoRestante = saldoRestante;
    }

    public int getPagosRealizados() {
        return pagosRealizados;
    }
    public void setPagosRealizados(int pagosRealizados) {
        this.pagosRealizados = pagosRealizados;
    }

    public double getTasaInteres() {
        return tasaInteres;
    }

    //otros metodos
    private long generarIdAleatorio() {
        return Math.abs(new Random().nextLong() % 1_000_000_000L) + 2_000_000_000L;
    }

    public String devolverMensaje(LoanStatus estado) {
        switch (estado) {
            case APROBADO:
                mensaje = "El préstamo fue aprobado.";
                break;
            case RECHAZADO:
                mensaje = "El préstamo ha sido rechazado debido a una calificación crediticia insuficiente";
                break;
            case CERRADO:
                mensaje = "El préstamo ha sido saldado.";
                break;
            default:
                mensaje = "Estado desconocido del préstamo";
                break;
        }
        return mensaje;
    }

    @Override
    public String toString() {
        return "Prestamo: " +
                "\nId del prestamo: " + id +
                "\nNumero de cliente: " + dniTitular +
                "\nMonto: " + monto +
                "\nMoneda: " + moneda +
                "\nPlazo en meses: " + plazoMeses +
                "\nEstado: " + loanStatus +
                "\nMensaje: " + mensaje +
                "\nSaldo restante: " + saldoRestante +
                "\nPagos realizados: " + pagosRealizados;
    }
}