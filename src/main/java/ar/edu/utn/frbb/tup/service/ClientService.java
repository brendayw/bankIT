package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDetailsDto;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDto;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.dto.LoansListDto;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.AccountRepository;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import ar.edu.utn.frbb.tup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository repository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public Client createClient(ClientDto dto, User user) {
        if (repository.existsByPersonDni(dto.person().dni())) {
            throw new ClientAlreadyExistsException("El cliente con DNI " + dto.person().dni() + " ya existe.");
        }
        Client client = new Client(dto);
        client.setUser(user);
        user.setClient(client);
        return repository.save(client);
    }

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

    //update cliente
    public ClientDetailsDto updateOwnClientDetails(User authenticatedUser, String telefono, String email) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new ValidationException("El usuario no tiene un cliente asociado");
        }
        if (telefono != null && !telefono.isBlank()) {
            client.getPerson().setTelefono(telefono);
        }
        if (email != null && !email.isBlank()) {
            client.getPerson().setEmail(email);
        }
        repository.save(client);
        return new ClientDetailsDto(client);
    }

    //desactivar cliente
    public void deactivateOwnClient(User authenticatedUser) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new ClientNotFoundException("El usuario no tiene un cliente asociado");
        }
        client.deactivate();
        repository.save(client);
    }
}