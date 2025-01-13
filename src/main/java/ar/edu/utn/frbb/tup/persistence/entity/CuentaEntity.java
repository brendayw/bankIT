package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

import java.time.LocalDate;

public class CuentaEntity extends BaseEntity {
    private long numeroCuenta;
    private Long titular; //dniTitular
    private final String tipoCuenta;
    private final String tipoMoneda;
    private double balance;
    private final LocalDate fechaCreacion;
    private boolean estado;

    public CuentaEntity(Cuenta cuenta) {
        super(cuenta.getNumeroCuenta());
        this.numeroCuenta = cuenta.getNumeroCuenta();
        this.titular = cuenta.getDniTitular();
        this.tipoCuenta = cuenta.getTipoCuenta() != null ? cuenta.getTipoCuenta().getDescripcion() : null;
        this.tipoMoneda = cuenta.getTipoMoneda() != null ? cuenta.getTipoMoneda().getDescripcion() : null;
        this.balance = cuenta.getBalance();
        this.fechaCreacion = cuenta.getFechaCreacion();
        this.estado = cuenta.isEstado();
    }

    public Cuenta toCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(this.numeroCuenta);
        cuenta.setDniTitular(this.titular);
        cuenta.setTipoCuenta(TipoCuenta.fromString(this.tipoCuenta));
        cuenta.setTipoMoneda(TipoMoneda.fromString(this.tipoMoneda));
        cuenta.setBalance(this.balance);
        cuenta.setFechaCreacion(this.fechaCreacion);
        cuenta.setEstado(true);
        return cuenta;
    }

    public long getNumeroCuenta() {
        return numeroCuenta;
    }
    public void setNumeroCuenta(long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Long getTitular() {
        return titular;
    }
    public void setTitular(Long titular) {
        this.titular = titular;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }
    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public boolean isEstado() {
        return estado;
    }
    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}