package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import org.springframework.stereotype.Component;

@Component
public class PrestamoValidator {

    public void validate(PrestamoDto prestamoDto) {
        if (prestamoDto.getMoneda() == null) {
            throw new IllegalArgumentException("El tipo de moneda no es correcto.");
        }
    }
}
