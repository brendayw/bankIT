package ar.edu.utn.frbb.tup.model.cliente.dto;

import ar.edu.utn.frbb.tup.model.persona.TipoPersona;
import ar.edu.utn.frbb.tup.model.persona.dto.PersonDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientDto(
        PersonDto persona,
        @NotNull @Valid TipoPersona tipoPersona,
        @NotBlank String banco) {
}