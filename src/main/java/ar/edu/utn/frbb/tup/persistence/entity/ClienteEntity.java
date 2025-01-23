package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.TipoPersona;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClienteEntity extends BaseEntity {
    private final String tipoPersona;
    private final String nombre;
    private final String apellido;
    private String telefono;
    private String email;
    private final LocalDate fechaAlta;
    private final LocalDate fechaNacimiento;
    private final String banco;
    private Set<Long> cuentas;
    private Set<Long> prestamos;
    private boolean activo;

    public ClienteEntity(Cliente cliente) {
        super(cliente.getDni());
        this.tipoPersona = cliente.getTipoPersona() != null ? cliente.getTipoPersona().getDescripcion() : null;
        this.nombre = cliente.getNombre();
        this.apellido = cliente.getApellido();
        this.telefono = cliente.getTelefono();
        this.email = cliente.getEmail();
        this.fechaAlta = cliente.getFechaAlta();
        this.fechaNacimiento = cliente.getFechaNacimiento();
        this.banco = cliente.getBanco();
        this.cuentas = new HashSet<>();
        this.prestamos = new HashSet<>();
        this.activo = cliente.isActivo();
        if (cliente.getCuentas() != null && !cliente.getCuentas().isEmpty()) {
            for (Cuenta c: cliente.getCuentas()) {
                cuentas.add(c.getNumeroCuenta());
            }
        }
        if (cliente.getPrestamos() != null && !cliente.getPrestamos().isEmpty()) {
            for (Prestamo p: cliente.getPrestamos()) {
                prestamos.add(p.getId());
            }
        }
    }

    public Cliente toCliente() {
        Cliente cliente = new Cliente();
        cliente.setDni(this.getId());
        cliente.setNombre(this.nombre);
        cliente.setApellido(this.apellido);
        cliente.setTelefono(this.telefono);
        cliente.setEmail(this.email);
        cliente.setTipoPersona(TipoPersona.fromString(this.tipoPersona));
        cliente.setFechaAlta(this.fechaAlta);
        cliente.setFechaNacimiento(this.fechaNacimiento);
        cliente.setBanco(this.banco);
        cliente.setActivo(true);
        return cliente;
    }

    //getters y setters
    public Set<Long> getCuentas() {
        return cuentas;
    }

    public void setCuentas(Set<Long> cuentas) {
        this.cuentas = cuentas;
    }

    public Set<Long> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(Set<Long> prestamos) {
        this.prestamos = prestamos;
    }
}
