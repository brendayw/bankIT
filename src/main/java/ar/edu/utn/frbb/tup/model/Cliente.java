package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoPersona;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Cliente extends Persona{
    private TipoPersona tipoPersona;
    private String banco;
    private LocalDate fechaAlta;
    private Set<Cuenta> cuentas = new HashSet<>();
    private Set<Prestamo> prestamos = new HashSet<>();
    private boolean activo;

    //constructores
    public Cliente() {
        super();
    }
    public Cliente(ClienteDto clienteDto) {
        super(clienteDto.getDni(), clienteDto.getApellido(), clienteDto.getNombre(), clienteDto.getFechaNacimiento(), clienteDto.getTelefono(), clienteDto.getEmail());
        tipoPersona = TipoPersona.fromString(clienteDto.getTipoPersona());
        fechaAlta = LocalDate.now();
        banco = clienteDto.getBanco();
        activo = true;
    }

    //getters y setters
    public TipoPersona getTipoPersona() {
        return tipoPersona;
    }
    public void setTipoPersona(TipoPersona tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getBanco() {
        return banco;
    }
    public void setBanco(String banco) {
        this.banco = banco;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }
    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Set<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(Set<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public Set<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(Set<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    //metodos
    public void addCuenta(Cuenta cuenta) {
        this.cuentas.add(cuenta);
        cuenta.setDniTitular(cuenta.getDniTitular());
    }

    public void addPrestamo(Prestamo prestamo) {
        this.prestamos.add(prestamo);
        prestamo.setDniTitular(prestamo.getDniTitular());
    }

    public boolean tieneCuentaEnTipoCuenta(TipoCuenta tipoCuenta) {
        for (Cuenta cuenta: cuentas) {
            if (tipoCuenta.equals(cuenta.getTipoCuenta())) {
                return true;
            }
        }
        return false;
    }

    public boolean tieneCuentaEnMoneda(TipoMoneda moneda) {
        for (Cuenta cuenta: cuentas) {
            if (moneda.equals(cuenta.getTipoMoneda())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Cliente: " +
                "\nTipoPersona=" + tipoPersona +
                "\nBanco='" + banco +
                "\nFecha de Alta=" + fechaAlta +
                "\nCuentas=" + cuentas;
    }
}
