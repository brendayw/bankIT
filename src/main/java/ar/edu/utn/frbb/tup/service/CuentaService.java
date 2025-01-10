package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaYaExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoCuentaYaExisteException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Cuenta darDeAltaCuenta(CuentaDto cuentaDto) throws CuentaYaExisteException, TipoCuentaYaExisteException, ClientNoExisteException {
        Cuenta cuenta = new Cuenta(cuentaDto);
        if(cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaYaExisteException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }
//        //Chequear cuentas soportadas por el banco CA$ CC$ CAU$S
//        if (!tipoCuentaEstaSoportada(cuenta)) {
//           throw new CuentaNoSoportadaException("El tipo de cuenta no es soportado.");
//        }
//        if (!tipoMonedaEstaSoportada(cuenta)) {
//            throw new TipoMonedaNoSoportada("El tipo de moneda no es soportado.");
//        }
        clienteService.agregarCuenta(cuenta, cuenta.getDniTitular());
        cuentaDao.save(cuenta);
        return cuenta;
    }


//    public boolean tipoCuentaEstaSoportada(Cuenta cuenta) {
//        boolean tipoCuentaSoportada;
//        tipoCuentaSoportada = cuenta.getTipoCuenta() == TipoCuenta.CUENTA_CORRIENTE || cuenta.getTipoCuenta() == TipoCuenta.CAJA_AHORRO;
//        return tipoCuentaSoportada;
//    }
//    public boolean tipoMonedaEstaSoportada(Cuenta cuenta) {
//        boolean tipoMonedaSoportada;
//        tipoMonedaSoportada = cuenta.getTipoMoneda() == TipoMoneda.DOLARES|| cuenta.getTipoMoneda() == TipoMoneda.PESOS;
//        return tipoMonedaSoportada;
//    }

    public Cuenta buscarCuentaPorId(long id) {
        Cuenta cuenta = cuentaDao.find(id);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no existe.");
        }
        return cuenta;
    }
}