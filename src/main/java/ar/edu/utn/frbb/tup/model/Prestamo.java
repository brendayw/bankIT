package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Prestamo {
    private long id;
    private long dniTitular;
    private double monto;
    private double montoConInteres;
    private TipoMoneda moneda; //lo marca como string
    private int plazoMeses;
    private LocalDate fechaSolicitud;
    private LoanStatus loanStatus; //lo marca como string
    private String mensaje;
    private List<PlanPago> planDePagos;

    //constructores
    public Prestamo() {
        this.id = generarIdAleatorio();
        this.fechaSolicitud = LocalDate.now();
        this.planDePagos = new ArrayList<>();
    }

    public Prestamo(PrestamoDto prestamoDto, int score) {
        this.id = generarIdAleatorio();
        this.dniTitular = prestamoDto.getNumeroCliente();
        this.monto = prestamoDto.getMontoPrestamo();
        this.moneda = TipoMoneda.fromString(prestamoDto.getTipoMoneda());
        this.plazoMeses = prestamoDto.getPlazoMeses();
        this.loanStatus = score >= 700 ? LoanStatus.APROBADO : LoanStatus.RECHAZADO;
        this.mensaje = devolverMensaje(this.loanStatus);
        this.planDePagos = calcularPlanPagos();
    }

    // getters & setters
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

    public double getMonto() {
        return monto;
    }
    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getMontoConInteres() {
        return montoConInteres;
    }
    public void setMontoConInteres(double montoConInteres) {
        this.montoConInteres = montoConInteres;
    }


    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }
    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
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

    //otros metodos
    private long generarIdAleatorio() {
        return Math.abs(new Random().nextLong() % 1_000_000_000L) + 2_000_000_000L;
    }

    public String devolverMensaje(LoanStatus estado) {
        switch (estado) {
            case APROBADO:
                mensaje = "El préstamo fue aprobado, pronto será desembolsado.";
                break;
            case RECHAZADO:
                mensaje = "El préstamo ha sido rechazado debido a una calificación crediticia insuficiente";
                break;
            default:
                mensaje = "Estado desconocido del préstamo";
                break;
        }
        return mensaje;
    }

    public List<PlanPago> calcularPlanPagos() {
        List<PlanPago> cuotas = new ArrayList<>();
        double montoCuota = this.monto / this.plazoMeses;
        for (int i = 1; i <= this.plazoMeses; i++) {
            cuotas.add(new PlanPago(i, montoCuota));
        }
        return cuotas;
    }

    @Override
    public String toString() {
        return "Prestamo: " +
                "\nId del prestamo: " + id +
                "\nNumero de cliente: " + dniTitular +
                "\nMonto: " + monto +
                "\nMoneda: " + moneda +
                //"\nMonto con Interes " + montoConInteres +
                "\nPlazo en meses: " + plazoMeses +
                "\nFecha de solicitud: " + fechaSolicitud +
                "\nEstado: " + loanStatus +
                "\nMensaje: " + mensaje;
    }
}