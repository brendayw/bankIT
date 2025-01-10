package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import org.springframework.stereotype.Component;

@Component
public class CuentaValidator {

    public void validate(CuentaDto cuentaDto) {
        validateTipoCuenta(cuentaDto);
        validateTipoMoneda(cuentaDto);
    }

    public void validateTipoCuenta(CuentaDto cuentaDto) {
        if (!"C".equals(cuentaDto.getTipoCuenta()) && !"A".equals(cuentaDto.getTipoCuenta())) {
            throw new IllegalArgumentException("El tipo de cuenta no es correcto. Ingrese C: Cuenta corriente o A: caja de ahorro.");
        }
    }

    public void validateTipoMoneda(CuentaDto cuentaDto) {
        if ((!"P".equals(cuentaDto.getTipoMoneda()) && !"D".equals(cuentaDto.getTipoMoneda()))) {
            throw new IllegalArgumentException("El tipo de moneda no es correcto.");
        }
    }
}
