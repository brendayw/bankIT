package ar.edu.utn.frbb.tup.model.cliente.dto;

import ar.edu.utn.frbb.tup.model.cliente.Client;
import ar.edu.utn.frbb.tup.model.persona.TipoPersona;

public record ClientsListDto(
        Long id,
        String apellido,
        String nombre,
        String telefono,
        String email,
        TipoPersona tipoPersona
) {
    public ClientsListDto(Client client) {
        this(
                client.getId(),
                client.getPersona().getApellido(),
                client.getPersona().getNombre(),
                client.getPersona().getTelefono(),
                client.getPersona().getEmail(),
                client.getTipoPersona()
        );
    }
}
