package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.infra.exception.ValidacionException;
import ar.edu.utn.frbb.tup.model.cliente.exceptions.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.cuenta.Account;
import ar.edu.utn.frbb.tup.model.cuenta.dto.AccountDetailsDto;
import ar.edu.utn.frbb.tup.model.cuenta.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.cuenta.dto.AccountsListDto;
import ar.edu.utn.frbb.tup.model.cuenta.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.model.cuenta.exceptions.CuentaYaExisteException;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.AccountRepository;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    private AccountRepository repository;

    @Autowired
    private ClientRepository clientRepository;

    public Account createAccount(Long clientDni, AccountDto dto) {
        var client = clientRepository.findByPersonaDni(clientDni)
                .orElseThrow(() -> new ClientNoExisteException("Cliente no encontrado con ID " + clientDni));
        boolean exists = repository.existsByClientPersonaDniAndTipoCuentaAndTipoMoneda(dto.dniTitular(),
                dto.tipoCuenta(), dto.tipoMoneda());
        if (exists) {
            throw new CuentaYaExisteException("Ya existe una cuenta de tipo " + dto.tipoCuenta() + " con moneda "
                    + dto.tipoMoneda());
        }
        Account account = new Account();
        account.setClient(client);
        client.getCuentas().add(account);
        return repository.save(account);
    }

    //obtener todas las cuentas
    public Page<AccountDto> findAllAccounts(Pageable pagination) {
        return repository.findAll(pagination).map(AccountDto::new);
    }

    //obtener cuenta por id
    public AccountDetailsDto getAccountById(Long id, User authenticatedUser) {
        var account = repository.findById(id)
                .orElseThrow(() -> new CuentaNoExisteException("Cuenta con ID " + id + " no encontrada"));
        var client = authenticatedUser.getClient();
        if (client == null || !account.getClient().getId().equals(client.getId())) {
            throw new ValidacionException("No tenés permiso para acceder a esta cuenta.");
        }

        return new AccountDetailsDto(account);
    }

    //obtener cuentas por dni del cliente
    public AccountsListDto findAllAccountsByAuthenticatedUser(User authenticatedUser, Pageable pagination) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new IllegalStateException("El usuario no tiene cliente asociado");
        }

        List<AccountDto> accounts = repository.findAccountsByClientPersonaDniAndActiveTrue(client.getPersona().getDni(),
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
                .orElseThrow(() -> new CuentaNoExisteException("Cuenta no encontrada o no tenés permiso para acceder."));

        if (!account.getClient().getId().equals(client.getId())) {
            throw new ValidacionException("No tenés permiso para desactivar esta cuenta.");
        }

        account.deactivate();
        repository.save(account);
    }


}
