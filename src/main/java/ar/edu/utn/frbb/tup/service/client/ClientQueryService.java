package ar.edu.utn.frbb.tup.service.client;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDetailsDto;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.dto.LoansListDto;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.AccountRepository;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientQueryService {

    private final ClientRepository repository;
    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;

    //obtener propio perfil
    public ClientDetailsDto getOwnClientProfile(User authenticatedUser) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new ValidationException("El usuario no tiene cliente asociado");
        }
        return new ClientDetailsDto(client);
    }

    //obtener todos los prestamos del cliente por dni
    public Page<LoansListDto> findAllLoansByAuthenticatedClient(User authenticatedUser, Pageable pagination) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new ValidationException("El usuario no tiene cliente asociado");
        }
        Page<Loan> loans = loanRepository.findByClientPersonDni(client.getPerson().getDni(), pagination);
        return loans.map(LoansListDto::new);
    }

    //obtener todas las cuentas del cliente por dni
    public Page<AccountDto> findAllAccountsByAuthenticatedClient(User authenticatedUser, Pageable pagination) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new ValidationException("El usuario no tiene cliente asociado");
        }
        Page<Account> accounts = accountRepository.findAccountsByClientPersonDniAndActiveTrue(
                client.getPerson().getDni(), pagination);
        return accounts.map(AccountDto::new);
    }
}