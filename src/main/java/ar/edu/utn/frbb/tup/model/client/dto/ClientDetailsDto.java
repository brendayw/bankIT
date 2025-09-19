package ar.edu.utn.frbb.tup.model.client.dto;

import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDetailsDto;
import ar.edu.utn.frbb.tup.model.person.enums.PersonType;
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
        PersonType personType,
        LocalDate fechaAlta,
        Set<AccountDetailsDto> cuentas,
        Set<LoanResponseDto> prestamos
) {
    public ClientDetailsDto(Client client) {
        this(
                client.getId(),
                client.getPerson().getDni(),
                client.getPerson().getApellido(),
                client.getPerson().getNombre(),
                client.getPerson().getTelefono(),
                client.getPerson().getEmail(),
                client.getPersonType(),
                client.getRegistrationDate(),
                client.getAccounts()
                        .stream()
                        .map(AccountDetailsDto::new)
                        .collect(Collectors.toSet()),
                client.getLoans()
                        .stream()
                        .map(LoanResponseDto::new)
                        .collect(Collectors.toSet())
        );
    }
}