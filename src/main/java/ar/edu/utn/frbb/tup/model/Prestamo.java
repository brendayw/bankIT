package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

import java.time.LocalDate;
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

    private double cuotaMensual;
    private int cuotasPagadas;
    private int cuotasRestantes;

    //constructores
    public Prestamo() {
        this.id = new Random().nextLong();
    }
    public Prestamo(long id, long dniTitular, double monto, double montoConInteres, int plazoMeses, TipoMoneda moneda, LocalDate solicitudFecha, LocalDate aprovacionFecha, LoanStatus loanStatus, double cuotaMensual, int cuotasPagadas, int cuotasRestantes) {
        this.id = id;
        this.dniTitular = dniTitular;
        this.monto = monto;
        this.montoConInteres = montoConInteres;
        this.plazoMeses = plazoMeses;
        this.moneda = moneda;
        this.solicitudFecha = solicitudFecha;
        this.aprovacionFecha = aprovacionFecha;
        this.loanStatus = loanStatus;
        this.cuotaMensual = cuotaMensual;
        this.cuotasPagadas = cuotasPagadas;
        this.cuotasRestantes = cuotasRestantes;
    }

    public Prestamo(PrestamoDto prestamoDto) {
        id = Math.abs(new Random().nextLong() % 1_000_000_000L) + 2_000_000_000L;
        dniTitular = prestamoDto.getDniTitular();
        monto = prestamoDto.getMontoPrestamo();
        moneda = TipoMoneda.fromString(prestamoDto.getTipoMoneda());
        plazoMeses = prestamoDto.getPlazoMeses();
        this.solicitudFecha = LocalDate.now();
        loanStatus = LoanStatus.PENDIENTE;
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

    public double getmontoConInteres() {
        return montoConInteres;
    }
    public void setmontoConInteres(double montoConInteres) {
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
    }

    public TipoMoneda getMoneda() {
        return moneda;
    }
    public void setMoneda(TipoMoneda moneda) {
        this.moneda = moneda;
    }

    public double getCuotaMensual() {
        return cuotaMensual;
    }
    public void setCuotaMensual(double cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }

    public int getCuotasPagadas() {
        return cuotasPagadas;
    }
    public void setCuotasPagadas(int cuotasPagadas) {
        this.cuotasPagadas = cuotasPagadas;
    }

    public int getCuotasRestantes() {
        return cuotasRestantes;
    }
    public void setCuotasRestantes(int cuotasRestantes) {
        this.cuotasRestantes = cuotasRestantes;
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
                "\nEstado: " + loanStatus +
                "\nMonto cuota mensual: " + cuotaMensual +
                "\nCuotas pagadas: " + cuotasPagadas +
                "\nCuotas restantes: " + cuotasRestantes;
    }
}