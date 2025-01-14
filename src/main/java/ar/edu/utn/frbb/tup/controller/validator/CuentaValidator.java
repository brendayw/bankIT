package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoMonedaNoSoportada;
import org.springframework.stereotype.Component;

@Component
public class CuentaValidator {

    public void validateCuenta(CuentaDto cuentaDto) throws CuentaNoSoportadaException, TipoMonedaNoSoportada, CampoIncorrecto {
        validateTipoCuenta(cuentaDto);
        validateTipoMoneda(cuentaDto);
        validateDatosCompletos(cuentaDto);
        validateDni(cuentaDto);
    }

    public void validateTipoCuenta(CuentaDto cuentaDto) throws CuentaNoSoportadaException {
        if (!"C".equals(cuentaDto.getTipoCuenta()) && !"A".equals(cuentaDto.getTipoCuenta())) {
            throw new CuentaNoSoportadaException("El tipo de cuenta no es correcto. Ingrese C: Cuenta corriente o A: caja de ahorro.");
        }
    }

    public void validateTipoMoneda(CuentaDto cuentaDto) throws TipoMonedaNoSoportada {
        if ((!"P".equals(cuentaDto.getTipoMoneda()) && !"D".equals(cuentaDto.getTipoMoneda()))) {
            throw new TipoMonedaNoSoportada("El tipo de moneda no es correcto. Ingrese P: pesos o D: dolares.");
        }
    }

    public void validateDatosCompletos(CuentaDto cuentaDto) throws CampoIncorrecto {
        if (cuentaDto.getBalance()  <= 0) {
            throw new CampoIncorrecto("El balance no puede ser 0 o nulo.");
        }
        if (cuentaDto.getTipoCuenta() == null || cuentaDto.getTipoCuenta().isEmpty()) {
            throw new CampoIncorrecto("El tipoCuenta no puede ser nulo.");
        }
        if (cuentaDto.getTipoMoneda() == null || cuentaDto.getTipoMoneda().isEmpty()) {
            throw new CampoIncorrecto("El tipoMoneda no puede ser nulo.");
        }
    }

    private void validateDni(CuentaDto cuentaDto) {
        long dni = cuentaDto.getDniTitular();
        if (dni <= 0) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o cero.");
        }

        if (dni < 00_000_000L || dni > 99_999_999L) { // Validar rango
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos y estar entre 00.000.000 y 99.999.999.");
        }
    }
}
