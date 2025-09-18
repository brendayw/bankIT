package ar.edu.utn.frbb.tup.model.client.dto;

import ar.edu.utn.frbb.tup.model.person.enums.TipoPersona;
import ar.edu.utn.frbb.tup.model.person.dto.PersonDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientDto(
        PersonDto persona,
        @NotNull @Valid TipoPersona tipoPersona,
        @NotBlank String banco) {
}