package ar.edu.utn.frbb.tup.service.account;

import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.account.exceptions.AccountAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.repository.AccountRepository;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountCreationService {

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
        client.addAccount(account);
        return repository.save(account);
    }
}
