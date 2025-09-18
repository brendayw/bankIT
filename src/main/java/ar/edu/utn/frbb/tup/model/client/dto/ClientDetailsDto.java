package ar.edu.utn.frbb.tup.model.client.dto;

import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDetailsDto;
import ar.edu.utn.frbb.tup.model.person.enums.TipoPersona;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanResponseDto;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

public record ClientDetailsDto(
        Long id,
        Long dni,
        String apellido,
        String nombre,
        String telefono,
        String email,
        TipoPersona tipoPersona,
        LocalDate fechaAlta,
        Set<AccountDetailsDto> cuentas,
        Set<LoanResponseDto> prestamos
) {
    public ClientDetailsDto(Client client) {
        this(
                client.getId(),
                client.getPersona().getDni(),
                client.getPersona().getApellido(),
                client.getPersona().getNombre(),
                client.getPersona().getTelefono(),
                client.getPersona().getEmail(),
                client.getTipoPersona(),
                client.getFechaAlta(),
                client.getCuentas()
                        .stream()
                        .map(AccountDetailsDto::new) // ⚡ aquí se usa el constructor correcto
                        .collect(Collectors.toSet()),
                client.getPrestamos()
                        .stream()
                        .map(LoanResponseDto::new) // convierte Loan → LoanResponseDto
                        .collect(Collectors.toSet())
        );
    }
}
