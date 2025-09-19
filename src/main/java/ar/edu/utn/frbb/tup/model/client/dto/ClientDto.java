package ar.edu.utn.frbb.tup.model.client.dto;

import ar.edu.utn.frbb.tup.model.person.enums.PersonType;
import ar.edu.utn.frbb.tup.model.person.dto.PersonDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ClientDto(
        PersonDto person,
        @NotNull @Valid PersonType personType) {
}