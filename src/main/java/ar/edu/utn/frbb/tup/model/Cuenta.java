package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Cuenta {
    private long numeroCuenta;
    private long dniTitular;
    private LocalDate fechaCreacion;
    private double balance;
    private TipoCuenta tipoCuenta;
    private TipoMoneda tipoMoneda;
    private boolean estado;

    //constructores
    public Cuenta() {
        this.numeroCuenta = new Random().nextLong();
    }
    public Cuenta(long numeroCuenta, long dniTitular, LocalDate fechaCreacion, double balance, TipoCuenta tipoCuenta, TipoMoneda tipoMoneda, boolean estado) {
        this.numeroCuenta = numeroCuenta;
        this.dniTitular = dniTitular;
        this.fechaCreacion = fechaCreacion;
        this.balance = balance;
        this.tipoCuenta = tipoCuenta;
        this.tipoMoneda = tipoMoneda;
        this.estado = estado;
    }

    public Cuenta(CuentaDto cuentaDto) {
        dniTitular = cuentaDto.getDniTitular();
        numeroCuenta = Math.abs(new Random().nextLong() % 1_000_000_000L) + 2_000_000_000L;
        tipoCuenta = TipoCuenta.fromString(cuentaDto.getTipoCuenta());
        tipoMoneda = TipoMoneda.fromString(cuentaDto.getTipoMoneda());
        this.balance = cuentaDto.getBalance();
        this.fechaCreacion = LocalDate.now();
        estado = true;
    }
    //getters y setters
    public long getNumeroCuenta() {
        return numeroCuenta;
    }
    public void setNumeroCuenta(long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public long getDniTitular() {
        return dniTitular;
    }
    public void setDniTitular(long dniTitular) {
        this.dniTitular = dniTitular;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }
    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public TipoMoneda getTipoMoneda() {
        return tipoMoneda;
    }
    public void setTipoMoneda(TipoMoneda tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isEstado() {
        return estado;
    }
    public void setEstado(boolean estado) {
        this.estado = estado;
    }

}