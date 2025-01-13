package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.*;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CuentaService {
    CuentaDao cuentaDao;

    @Autowired
    ClienteService clienteService;

    //Generar casos de test para darDeAltaCuenta
    //    1 - cuenta existente
    //    2 - cuenta no soportada
    //    3 - cliente ya tiene cuenta de ese tipo
    //    4 - cuenta creada exitosamente
    public CuentaService(CuentaDao cuentaDao) {
        this.cuentaDao = cuentaDao;
    }

    //agregar tipocuentayaexiste
    public Cuenta darDeAltaCuenta(CuentaDto cuentaDto) throws CuentaYaExisteException, TipoCuentaYaExisteException, ClientNoExisteException, CuentaNoSoportadaException, TipoMonedaNoSoportada {
        Cuenta cuenta = new Cuenta(cuentaDto);
        long numeroCuenta = cuenta.getNumeroCuenta();
        while (cuentaDao.find(numeroCuenta) != null) {
            numeroCuenta = cuenta.getNumeroCuenta();
        }
        cuenta.setNumeroCuenta(numeroCuenta);
        if(cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaYaExisteException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }
        //Chequear cuentas soportadas por el banco CA$ CC$ CAU$S
        if (!tipoCuentaEstaSoportada(cuenta)) {
           throw new CuentaNoSoportadaException("El tipo de cuenta no es soportado.");
        }
        if (!tipoMonedaEstaSoportada(cuenta)) {
            throw new TipoMonedaNoSoportada("El tipo de moneda no es soportado.");
        }
        clienteService.agregarCuenta(cuenta, cuenta.getDniTitular());
        cuentaDao.save(cuenta);
        return cuenta;
    }

    public Cuenta buscarCuentaPorId(long id) throws CuentaNoExisteException {
        Cuenta cuenta = cuentaDao.find(id);
        if (cuenta == null) {
            throw new CuentaNoExisteException("La cuenta no existe.");
        }
        return cuenta;
    }

    public List<Cuenta> buscarCuentaPorCliente(long dni) throws ClientNoExisteException, CuentaNoExisteException {
        Cliente cliente = clienteService.buscarClientePorDni(dni);
        if (cliente == null) {
            throw  new ClientNoExisteException("El cliente con DNI: " + dni + " no existe.");
        }
        Set<Cuenta> cuentas = cliente.getCuentas();
        if (cuentas == null || cuentas.isEmpty()) {
            throw new CuentaNoExisteException("El cliente con DNI: " + dni + " no tiene cuentas asociadas.");
        }
        return new ArrayList<>(cuentas);
    }

    public List<Cuenta> buscarCuentas() {
        return cuentaDao.findAll();
    }
    //actualiza balance y estado
    public Cuenta actulizarDatosCuenta(long id, double nuevoBalance, Boolean estado) throws CuentaNoExisteException {
        Cuenta cuenta = cuentaDao.find(id);
        if (cuenta == null) {
            throw new CuentaNoExisteException("La cuenta con ID: " + id + " no existe.");
        }
        if (nuevoBalance != 0.0) {
            cuenta.setBalance(nuevoBalance);
        }
        if (estado == null) {
            cuenta.setEstado(true);
        }
        cuentaDao.update(cuenta);
        return cuenta;
    }

    //delete
    public Cuenta desactivarCuenta(long id) throws CuentaNoExisteException {
        Cuenta cuenta = cuentaDao.find(id);
        if (cuenta == null) {
            throw new CuentaNoExisteException("La cuenta con ID: " + id + " no existe.");
        }
        cuenta.setEstado(false);
        actulizarDatosCuenta(id, 0.0, false);
        return cuenta;
    }

    //otro metodos
    public boolean tipoCuentaEstaSoportada(Cuenta cuenta) {
        boolean tipoCuentaSoportada;
        tipoCuentaSoportada = cuenta.getTipoCuenta() == TipoCuenta.CUENTA_CORRIENTE || cuenta.getTipoCuenta() == TipoCuenta.CAJA_AHORRO;
        return tipoCuentaSoportada;
    }
    public boolean tipoMonedaEstaSoportada(Cuenta cuenta) {
        boolean tipoMonedaSoportada;
        tipoMonedaSoportada = cuenta.getTipoMoneda() == TipoMoneda.DOLARES|| cuenta.getTipoMoneda() == TipoMoneda.PESOS;
        return tipoMonedaSoportada;
    }
}