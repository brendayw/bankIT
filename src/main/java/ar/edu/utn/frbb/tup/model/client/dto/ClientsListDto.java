package ar.edu.utn.frbb.tup.model.client.dto;

import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.person.enums.PersonType;

public record ClientsListDto(
        Long id,
        String apellido,
        String nombre,
        String telefono,
        String email,
        PersonType personType
) {
}