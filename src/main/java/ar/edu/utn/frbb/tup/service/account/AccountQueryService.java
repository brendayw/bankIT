package ar.edu.utn.frbb.tup.service.account;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDetailsDto;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.account.dto.AccountsListDto;
import ar.edu.utn.frbb.tup.model.account.exceptions.AccountNotFoundException;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.AccountRepository;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountQueryService {

    private final AccountRepository repository;
    private final ClientRepository clientRepository;

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
}
