package ar.edu.utn.frbb.tup.service.imp;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.*;

import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import ar.edu.utn.frbb.tup.service.ClienteService;
import ar.edu.utn.frbb.tup.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CuentaServiceImp implements CuentaService {
    @Autowired  CuentaDao cuentaDao;
    @Autowired ClienteService clienteService;

    public CuentaServiceImp(CuentaDao cuentaDao, ClienteService clienteService) {
        this.cuentaDao = cuentaDao;
        this.clienteService = clienteService;
    }

    //agregar tipocuentayaexiste
    @Override
    public Cuenta darDeAltaCuenta(CuentaDto cuentaDto) throws CuentaYaExisteException, TipoCuentaYaExisteException, ClientNoExisteException, CuentaNoSoportadaException, TipoMonedaNoSoportada {
        Cuenta cuenta = new Cuenta(cuentaDto);
        validarCuentaUnica(cuenta);
        validarTipoCuentaUnica(cuenta);
        validarTipoCuenta(cuenta);
        validarTipoMoneda(cuenta);
        clienteService.agregarCuenta(cuenta, cuenta.getDniTitular());
        cuentaDao.save(cuenta);
        return cuenta;
    }

    @Override
    public Cuenta buscarCuentaPorId(long id) throws CuentaNoExisteException {
        return obtenerCuentaExistente(id);
    }

    @Override
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

    @Override
    public List<Cuenta> buscarCuentas() {
        return cuentaDao.findAll();
    }

    //actualiza balance y estado
    @Override
    public Cuenta actulizarDatosCuenta(long id, double nuevoBalance, Boolean estado) throws CuentaNoExisteException {
        Cuenta cuenta = obtenerCuentaExistente(id);
        if (nuevoBalance != 0.0) {
            cuenta.setBalance(nuevoBalance);
        }
        if (estado == null) {
            cuenta.setEstado(true);
        }
        cuentaDao.update(cuenta);
        return cuenta;
    }

    //actualizar si el prestamo se aprueba
    @Override
    public void actualizarBalance(Prestamo prestamo) throws CuentaNoExisteException {
        Cuenta cuenta = cuentaDao.findByClienteYTipoMoneda(prestamo.getDniTitular(), prestamo.getMoneda().toString());
        if (cuenta == null) {
            throw new CuentaNoExisteException("La cuenta no existe.");
        }
        double nuevoBalance = cuenta.getBalance() + prestamo.getMontoSolicitado();
        cuenta.setBalance(nuevoBalance);
        cuentaDao.save(cuenta);
    }

    //delete
    @Override
    public Cuenta desactivarCuenta(long id) throws CuentaNoExisteException {
        Cuenta cuenta = obtenerCuentaExistente(id);
        cuenta.setEstado(false);
        cuentaDao.update(cuenta);
        return cuenta;
    }

    //otro metodos
    private void validarCuentaUnica(Cuenta cuenta) throws CuentaYaExisteException {
        if (cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaYaExisteException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }
    }

    private void validarTipoCuentaUnica(Cuenta cuenta) throws TipoCuentaYaExisteException {
        List<Cuenta> cuentasCliente = cuentaDao.buscarCuentasByCliente(cuenta.getDniTitular());
        for (Cuenta cuentas : cuentasCliente) {
            if (cuentas.getTipoCuenta() == cuenta.getTipoCuenta() && cuentas.getTipoMoneda() == cuenta.getTipoMoneda()) {
                throw new TipoCuentaYaExisteException("El cliente ya tiene una cuenta de tipo " + cuenta.getTipoCuenta() + " en " + cuenta.getTipoMoneda() + ".");
            }
        }
    }

    private void validarTipoCuenta(Cuenta cuenta) throws CuentaNoSoportadaException {
        if (!tipoCuentaEstaSoportada(cuenta)) {
            throw new CuentaNoSoportadaException("El tipo de cuenta no es soportado.");
        }
    }

    private void validarTipoMoneda(Cuenta cuenta) throws TipoMonedaNoSoportada {
        if (!tipoMonedaEstaSoportada(cuenta)) {
            throw new TipoMonedaNoSoportada("El tipo de moneda no es soportado.");
        }
    }

    private Cuenta obtenerCuentaExistente(long id) throws CuentaNoExisteException {
        Cuenta cuenta = cuentaDao.find(id);
        if (cuenta == null) {
            throw new CuentaNoExisteException("La cuenta con ID: " + id + " no existe.");
        }
        return cuenta;
    }

    private boolean tipoCuentaEstaSoportada(Cuenta cuenta) {
        return cuenta.getTipoCuenta() == TipoCuenta.CUENTA_CORRIENTE || cuenta.getTipoCuenta() == TipoCuenta.CAJA_AHORRO;
    }

    private boolean tipoMonedaEstaSoportada(Cuenta cuenta) {
        return cuenta.getTipoMoneda() == TipoMoneda.DOLARES || cuenta.getTipoMoneda() == TipoMoneda.PESOS;
    }
}
