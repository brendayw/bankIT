package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoMonedaNoSoportada;
import org.springframework.stereotype.Component;

@Component
public class PrestamoValidator {

    public void validatePrestamo(PrestamoDto prestamoDto) throws TipoMonedaNoSoportada, CampoIncorrecto {
        validateTipoMoneda(prestamoDto);
        validateDatosCompletos(prestamoDto);
        validateDni(prestamoDto);
        //validateEstaedo(prestamoDto);
    }

    public void validateTipoMoneda(PrestamoDto prestamoDto) throws TipoMonedaNoSoportada {
        if (!"P".equals(prestamoDto.getTipoMoneda()) && !"D".equals(prestamoDto.getTipoMoneda())) {
            throw new TipoMonedaNoSoportada("El tipo de moneda no es correcto. Ingrese P: pesos o D: dolares");
        }
    }

    public void validateDatosCompletos(PrestamoDto prestamoDto) throws CampoIncorrecto {
        if (prestamoDto.getMontoPrestamo() <= 0) {
            throw new CampoIncorrecto("El monto del prestamo no puede ser 0 o nulo.");
        }
        if (prestamoDto.getTipoMoneda() == null || prestamoDto.getTipoMoneda().isEmpty()) {
            throw new CampoIncorrecto("El tipo de moneda no puede ser nulo.");
        }
        if (prestamoDto.getPlazoMeses() <= 0) {
            throw new CampoIncorrecto("El plazo no puede ser 0 o nulo.");
        }
    }

    private void validateDni(PrestamoDto prestamoDto) {
        long dni = prestamoDto.getDniTitular();
        if (dni <= 0) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o cero.");
        }
        if (dni < 00_000_000L || dni > 99_999_999L) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos y estar entre 00.000.000 y 99.999.999.");
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
