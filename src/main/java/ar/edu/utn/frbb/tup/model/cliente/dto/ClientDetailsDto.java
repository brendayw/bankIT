package ar.edu.utn.frbb.tup.model.cliente.dto;

import ar.edu.utn.frbb.tup.model.cliente.Client;
import ar.edu.utn.frbb.tup.model.cuenta.dto.AccountDetailsDto;
import ar.edu.utn.frbb.tup.model.cuenta.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.persona.TipoPersona;
import ar.edu.utn.frbb.tup.model.prestamo.dto.LoanDetailsDto;
import ar.edu.utn.frbb.tup.model.prestamo.dto.LoanResponseDto;

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
