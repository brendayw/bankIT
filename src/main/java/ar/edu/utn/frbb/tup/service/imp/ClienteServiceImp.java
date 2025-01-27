package ar.edu.utn.frbb.tup.service.imp;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteMayorDeEdadException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoCuentaYaExisteException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import ar.edu.utn.frbb.tup.service.ClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImp implements ClienteService {
    @Autowired ClienteDao clienteDao;
    @Autowired CuentaDao cuentaDao;

    public ClienteServiceImp(ClienteDao clienteDao, CuentaDao cuentaDao) {
        this.clienteDao = clienteDao;
        this.cuentaDao = cuentaDao;
    }

    //da de alta el cliente
    public Cliente darDeAltaCliente(ClienteDto clienteDto) throws ClienteAlreadyExistsException, ClienteMayorDeEdadException {
        Cliente cliente = new Cliente(clienteDto);
        verificarClienteExistente(cliente.getDni());
        verificarEdadValida(cliente.getEdad());
        clienteDao.save(cliente);
        return cliente;
    }

    //busca cliente por dni
    public Cliente buscarClientePorDni(Long dni) throws ClientNoExisteException {
        Cliente cliente = clienteDao.find(dni, true);
        if (cliente == null) {
            throw new ClientNoExisteException("El cliente no existe");
        }
        return cliente;
    }

    //buscar todos los clientes
    public List<Cliente> buscarClientes() {
        return clienteDao.findAll();
    }

    //delete
    public Cliente desactivarCliente(Long dni) {
        Cliente cliente = clienteDao.find(dni, true);
        cliente.setActivo(false);
        clienteDao.save(cliente);
        return cliente;
    }

    //otros metodos
    private void verificarClienteExistente(long dni) throws ClienteAlreadyExistsException {
        if (clienteDao.find(dni, false) != null) {
            throw new ClienteAlreadyExistsException("Ya existe un cliente con ese DNI.");
        }
    }

    private void verificarEdadValida(int edad) throws ClienteMayorDeEdadException {
        if (edad < 18) {
            throw new ClienteMayorDeEdadException("El cliente debe ser mayor a 18 años");
        }
    }

    private void verificarTipoCuentaExistente(Cliente titular, Cuenta cuenta) throws TipoCuentaYaExisteException {
        if (titular.tieneCuenta(cuenta.getTipoCuenta(), cuenta.getTipoMoneda())) {
            throw new TipoCuentaYaExisteException("El cliente ya posee una cuenta de ese tipo y moneda");
        }
    }

    //agrega una cuenta
    public void agregarCuenta(Cuenta cuenta, long dniTitular) throws TipoCuentaYaExisteException {
        Cliente titular = clienteDao.find(dniTitular, true);
        verificarTipoCuentaExistente(titular, cuenta);
        titular.getCuentas().add(cuenta);
        clienteDao.save(titular);
    }

    //agrega el prestamo
    public void agregarPrestamo(Prestamo prestamo, Long dniTitular) throws ClientNoExisteException {
        Cliente titular = buscarClientePorDni(dniTitular);
        titular.getPrestamos().add(prestamo);
        clienteDao.save(titular);
    }

    private void actualizarDatos(Cliente cliente, String nuevoTelefono, String nuevoEmail, Boolean activo) {
        if (nuevoTelefono != null && !nuevoTelefono.isEmpty()) {
            cliente.setTelefono(nuevoTelefono);
        }
        if (nuevoEmail != null && !nuevoEmail.isEmpty()) {
            cliente.setEmail(nuevoEmail);
        }
        if (activo != null) {
            cliente.setActivo(activo);
        }
    }

}
