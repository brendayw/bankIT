package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.exception.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    ClienteDao clienteDao;
    PrestamoService prestamoService;

    public ClienteService(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    //da de alta el cliente
    public Cliente darDeAltaCliente(ClienteDto clienteDto) throws ClienteAlreadyExistsException {
        Cliente cliente = new Cliente(clienteDto);
        if (clienteDao.find(cliente.getDni(), false) != null) {
            throw new ClienteAlreadyExistsException("Ya existe un cliente con DNI " + cliente.getDni());
        }
        if (cliente.getEdad() < 18) {
            throw new IllegalArgumentException("El cliente debe ser mayor a 18 años");
        }
        clienteDao.save(cliente);
        return cliente;
    }

    //agrega una cuenta
    public void agregarCuenta(Cuenta cuenta, long dniTitular) throws TipoCuentaAlreadyExistsException, ClientNoExisteException {
        Cliente titular = buscarClientePorDni(dniTitular);
        cuenta.setTitular(titular);
        if (titular.tieneCuenta(cuenta.getTipoCuenta(), cuenta.getMoneda())) {
            throw new TipoCuentaAlreadyExistsException("El cliente ya posee una cuenta de ese tipo y moneda");
        }
        titular.addCuenta(cuenta);
        clienteDao.save(titular);
    }

    //agrega el prestamo
    public void agregarPrestamo(Prestamo prestamo, long dniTitular) throws ClientNoExisteException {
        Cliente titular = buscarClientePorDni(dniTitular);
        prestamo.setNumeroCliente(titular);
        PrestamoDto prestamoDto = new PrestamoDto(prestamo);
        prestamoService.solicitarPrestamo(prestamoDto);
        titular.addPrestamo(prestamo);
        clienteDao.save(titular);
    }

    //busca cliente por dni
    public Cliente buscarClientePorDni(long dni) throws ClientNoExisteException {
        Cliente cliente = clienteDao.find(dni, true);
        if(cliente == null) {
            throw new ClientNoExisteException("El cliente no existe");
        }
        return cliente;
    }

    //update -> actualiza datos del cliente
    public Cliente actualizarDatosDelCliente(long dni, String nuevoTelefono, String nuevoEmail, Boolean activo) throws ClientNoExisteException {
        Cliente cliente = clienteDao.find(dni, true);
        if (cliente == null) {
            throw new ClientNoExisteException("El cliente no existe.");
        }
        //actualiza telefono
        if (nuevoTelefono != null && !nuevoTelefono.isEmpty()) {
            cliente.setTelefono(nuevoTelefono);
        }
        //actualiza mail
        if (nuevoEmail != null && !nuevoTelefono.isEmpty()) {
            cliente.setEmail(nuevoEmail);
        }
        //actualiza estado de cliente
        if (activo == null) {
            cliente.setActivo(activo);
        }
        clienteDao.update(cliente);
        return cliente;
    }

    //delete
    public Cliente desactivarCliente(long dni) throws ClientNoExisteException {
        Cliente cliente = clienteDao.find(dni, true);
        if (cliente == null) {
            throw new ClientNoExisteException("El cliente no existe, por ende no se puede desactivar.");
        }
        cliente.setActivo(false);
        actualizarDatosDelCliente(dni,null, null, false);
        return cliente;
    }

}
