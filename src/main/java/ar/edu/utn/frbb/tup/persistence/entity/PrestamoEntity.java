package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;


import java.time.LocalDateTime;

public class PrestamoEntity extends BaseEntity {
    long numeroCliente;
    double monto;
    double cuotaMensual;
    int plazoMeses;
    int cuotasPagadas;
    int cuotasRestantes;
    LocalDateTime solicitudFecha;
    String aprobacionFecha;
    LoanStatus estado;

    public PrestamoEntity(Prestamo prestamo) {
        super(prestamo.getId_loan());
        this.numeroCliente = prestamo.getNumeroCliente().getDni();
        this.monto = prestamo.getAmount();
        this.cuotaMensual = prestamo.getCuotaMensual();
        this.plazoMeses = prestamo.getTermMonths();
        this.cuotasPagadas = prestamo.getCuotasPagadas();
        this.cuotasRestantes = prestamo.getCuotasRestantes();
        this.aprobacionFecha = prestamo.getApprovalDate();
        this.estado = prestamo.getLoanStatus();
    }

    public Prestamo toPrestamo() {
        Prestamo prestamo = new Prestamo();
        prestamo.setAmount(this.monto);
        prestamo.setCuotaMensual(this.cuotaMensual);
        prestamo.setTermMonths(this.plazoMeses);
        prestamo.setCuotasPagadas(this.cuotasPagadas);
        prestamo.setCuotasRestantes(this.cuotasRestantes);
        prestamo.setApprovalDate(this.aprobacionFecha);
        prestamo.setLoanStatus(this.estado);
        return prestamo;
    }

    //getters y setters
    public long getNumeroCliente() {
        return numeroCliente;
    }
    public void setNumeroCliente(long numeroCliente) {
        this.numeroCliente = numeroCliente;
    }

    public double getMonto() {
        return monto;
    }
    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getCuotaMensual() {
        return cuotaMensual;
    }
    public void setCuotaMensual(double cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }

    public int getPlazoMeses() {
        return plazoMeses;
    }
    public void setPlazoMeses(int plazoMeses) {
        this.plazoMeses = plazoMeses;
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

    public LocalDateTime getSolicitudFecha() {
        return solicitudFecha;
    }
    public void setSolicitudFecha(LocalDateTime solicitudFecha) {
        this.solicitudFecha = solicitudFecha;
    }

    public String getAprobacionFecha() {
        return aprobacionFecha;
    }
    public void setAprobacionFecha(String aprobacionFecha) {
        this.aprobacionFecha = aprobacionFecha;
    }

    public LoanStatus getEstado() {
        return estado;
    }
    public void setEstado(LoanStatus estado) {
        this.estado = estado;
    }
}
