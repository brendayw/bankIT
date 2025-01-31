package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.*;

import java.util.List;

public interface CuentaService {
    Cuenta darDeAltaCuenta(CuentaDto cuentaDto) throws CuentaYaExisteException, TipoCuentaYaExisteException, ClientNoExisteException, CuentaNoSoportadaException, TipoMonedaNoSoportada;
    Cuenta buscarCuentaPorId(long id) throws CuentaNoExisteException;
    List<Cuenta> buscarCuentaPorCliente(long dni) throws ClientNoExisteException, CuentaNoExisteException;
    List<Cuenta> buscarCuentas() throws CuentaNoExisteException;
    void actualizarBalance(Prestamo prestamo) throws CuentaNoExisteException;
    Cuenta desactivarCuenta(long id) throws CuentaNoExisteException;
}