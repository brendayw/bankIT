package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDetailsDto;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.account.dto.AccountsListDto;
import ar.edu.utn.frbb.tup.model.account.exceptions.AccountNotFoundException;
import ar.edu.utn.frbb.tup.model.account.exceptions.AccountAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.AccountRepository;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final ClientRepository clientRepository;

    public Account createAccount(Long clientDni, AccountDto dto) {
        var client = clientRepository.findByPersonDni(clientDni)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado con ID " + clientDni));

        boolean exists = repository.existsByClientPersonDniAndAccountTypeAndCurrencyType(dto.dni(),
                dto.accountType(), dto.currencyType());

        if (exists) {
            throw new AccountAlreadyExistsException("Ya existe una cuenta de tipo " + dto.currencyType() + " con moneda "
                    + dto.accountType());
        }
        Account account = Account.builder()
                .client(client)
                .creationDate(LocalDate.now())
                .balance(dto.balance() != null ? dto.balance() : 0.0)
                .accountType(dto.accountType())
                .currencyType(dto.currencyType())
                .active(true)
                .build();
        client.getAccounts().add(account);
        return repository.save(account);
    }

    //obtener cuenta por id
    public AccountDetailsDto findAccountById(Long id, User authenticatedUser) {
        var account = repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta con ID " + id + " no encontrada"));

        var client = authenticatedUser.getClient();
        if (client == null || !account.getClient().getId().equals(client.getId())) {
            throw new ValidationException("No tenés permiso para acceder a esta cuenta.");
        }
        return new AccountDetailsDto(account);
    }

    //obtener cuentas por dni del cliente
    public AccountsListDto findAllAccountsByAuthenticatedUser(User authenticatedUser, Pageable pagination) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new IllegalStateException("El usuario no tiene cliente asociado");
        }
        List<AccountDto> accounts = repository.findAccountsByClientPersonDniAndActiveTrue(client.getPerson().getDni(),
                        pagination).stream()
                .map(AccountDto::new)
                .toList();
        return new AccountsListDto(accounts);
    }

    //desactivar cuenta
    public void deactivateAccount(User authenticatedUser, Long id) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new IllegalStateException("El usuario no tiene cliente asociado");
        }

        var account = repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada o no tenés permiso para acceder."));
        if (!account.getClient().getId().equals(client.getId())) {
            throw new ValidationException("No tenés permiso para desactivar esta cuenta.");
        }
        account.deactivate();
        repository.save(account);
    }
}