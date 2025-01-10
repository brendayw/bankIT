package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

import java.time.LocalDate;

public class CuentaEntity extends BaseEntity {
    private long titular; //dniTitular
    private final String tipoCuenta;
    private final String tipoMoneda;
    private double balance;
    private final LocalDate fechaCreacion;
    private boolean estado;

    public CuentaEntity(Cuenta cuenta) {
        super(cuenta.getNumeroCuenta());
        this.titular = cuenta.getDniTitular();
        this.tipoCuenta = cuenta.getTipoCuenta() != null ? cuenta.getTipoCuenta().getDescripcion() : null;
        this.tipoMoneda = cuenta.getTipoMoneda() != null ? cuenta.getTipoCuenta().getDescripcion() : null;
        this.balance = cuenta.getBalance();
        this.fechaCreacion = cuenta.getFechaCreacion();
        this.estado = cuenta.isEstado();
    }

    public Cuenta toCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(cuenta.getNumeroCuenta());
        cuenta.setDniTitular(this.titular);
        cuenta.setTipoCuenta(TipoCuenta.fromString(this.tipoCuenta));
        cuenta.setTipoMoneda(TipoMoneda.fromString(this.tipoMoneda));
        cuenta.setBalance(this.balance);
        cuenta.setFechaCreacion(this.fechaCreacion);
        cuenta.setEstado(true);
        return cuenta;
    }
}