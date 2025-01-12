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
    private int plazoMeses;
    private TipoMoneda moneda;
    private LocalDate solicitudFecha;
    private LocalDate aprovacionFecha;
    private LoanStatus loanStatus;
    private String mensaje;
    //private double cuotaMensual;
    //private int cuotasPagadas;
    //private int cuotasRestantes;
    private List<PlanPago> planDePagos;
//    private String mensaje;

    //constructores
    public Prestamo() {
        this.id = new Random().nextLong();
    }

    public Prestamo(long id, long dniTitular, double monto, double montoConInteres, int plazoMeses, TipoMoneda moneda,
                    LocalDate solicitudFecha, LocalDate aprovacionFecha, LoanStatus loanStatus, String mensaje, double cuotaMensual,
                    int cuotasPagadas, int cuotasRestantes, List<PlanPago> planDePagos) {
        this.id = id;
        this.dniTitular = dniTitular;
        this.monto = monto;
        this.montoConInteres = montoConInteres;
        this.plazoMeses = plazoMeses;
        this.moneda = moneda;
        this.solicitudFecha = solicitudFecha;
        this.aprovacionFecha = aprovacionFecha;
        this.loanStatus = loanStatus;
        this.mensaje = mensaje;
        //this.cuotaMensual = cuotaMensual;
        //this.cuotasPagadas = cuotasPagadas;
        //this.cuotasRestantes = cuotasRestantes;
        this.planDePagos = new ArrayList<>();
    }

    public Prestamo(PrestamoDto prestamoDto, int score) {
        id = Math.abs(new Random().nextLong() % 1_000_000_000L) + 2_000_000_000L;
        dniTitular = prestamoDto.getDniTitular();
        monto = prestamoDto.getMontoPrestamo();
        moneda = TipoMoneda.fromString(prestamoDto.getTipoMoneda());
        plazoMeses = prestamoDto.getPlazoMeses();
        this.solicitudFecha = LocalDate.now();
        if (score >= 700) {
            this.loanStatus = LoanStatus.APROBADO;
        } else {
            this.loanStatus = LoanStatus.RECHAZADO;
        }
        mensaje = devolverMensaje(this.loanStatus);
        planDePagos = calcularPlanPagos();
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

    public int getPlazoMeses() {
        return plazoMeses;
    }
    public void setPlazoMeses(int plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public LocalDate getSolicitudFecha() {
        return solicitudFecha;
    }
    public void setSolicitudFecha(LocalDate solicitudFecha) {
        this.solicitudFecha = solicitudFecha;
    }

    public LocalDate getAprovacionFecha() {
        return aprovacionFecha;
    }
    public void setAprovacionFecha(LocalDate aprovacionFecha) {
        this.aprovacionFecha = aprovacionFecha;
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

    //otros metodos
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

//    public double getCuotaMensual() {
//        return cuotaMensual;
//    }
//    public void setCuotaMensual(double cuotaMensual) {
//        this.cuotaMensual = cuotaMensual;
//    }
//
//    public int getCuotasPagadas() {
//        return cuotasPagadas;
//    }
//    public void setCuotasPagadas(int cuotasPagadas) {
//        this.cuotasPagadas = cuotasPagadas;
//    }
//
//    public int getCuotasRestantes() {
//        return cuotasRestantes;
//    }
//    public void setCuotasRestantes(int cuotasRestantes) {
//        this.cuotasRestantes = cuotasRestantes;
//    }

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

    @Override
    public String toString() {
        return "Prestamo: " +
                "\nId del prestamo: " + id +
                "\nNumero de cliente: " + dniTitular +
                "\nMonto total: " + monto +
                "\nMoneda: " + moneda +
                "\nMonto con Interes " + montoConInteres +
                "\nPlazo en meses: " + plazoMeses +
                "\nFecha de solicitud: " + solicitudFecha +
                "\nFecha de aprovacion: " + aprovacionFecha +
                "\nEstado: " + loanStatus;
//                "\nMonto cuota mensual: " + cuotaMensual +
//                "\nCuotas pagadas: " + cuotasPagadas +
//                "\nCuotas restantes: " + cuotasRestantes;
    }
}