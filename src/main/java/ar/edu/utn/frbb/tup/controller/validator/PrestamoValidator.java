package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoMonedaNoSoportada;
import org.springframework.stereotype.Component;

@Component
public class PrestamoValidator {

    public void validate(PrestamoDto prestamoDto) throws TipoMonedaNoSoportada {
        validateTipoMoneda(prestamoDto);
        //validateEstaedo(prestamoDto);
    }

    public void validateTipoMoneda(PrestamoDto prestamoDto) throws TipoMonedaNoSoportada {
        if (!"P".equals(prestamoDto.getTipoMoneda()) && !"D".equals(prestamoDto.getTipoMoneda())) {
            throw new TipoMonedaNoSoportada("El tipo de moneda no es correcto. Ingrese P: pesos o D: dolares");
        }
    }

//    public void validateEstaedo(PrestamoDto prestamoDto) {
//        if (!"P".equals(prestamoDto.getEstado()) && !"A".equals(prestamoDto.getEstado()) &&
//                !"R".equals(prestamoDto.getEstado()) && !"D".equals(prestamoDto.getEstado()) &&
//                !"C".equals(prestamoDto.getEstado()) ) {
//            throw new IllegalArgumentException("El estado del prestamo ingresado no es correcto.");
//        }
//    }
}
