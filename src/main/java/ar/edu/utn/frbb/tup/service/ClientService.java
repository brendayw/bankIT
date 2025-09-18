package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.infra.exception.ValidacionException;
import ar.edu.utn.frbb.tup.model.cliente.Client;
import ar.edu.utn.frbb.tup.model.cliente.dto.ClientDetailsDto;
import ar.edu.utn.frbb.tup.model.cliente.dto.ClientDto;
import ar.edu.utn.frbb.tup.model.cliente.dto.ClientsListDto;
import ar.edu.utn.frbb.tup.model.cliente.exceptions.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.cliente.exceptions.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.cuenta.Account;
import ar.edu.utn.frbb.tup.model.cuenta.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.prestamo.Loan;
import ar.edu.utn.frbb.tup.model.prestamo.dto.LoansListDto;
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
        if (repository.existsByPersonaDni(dto.persona().dni())) {
            throw new ClienteAlreadyExistsException("El cliente con DNI " + dto.persona().dni() + " ya existe.");
        }

        Client client = new Client(dto);
        client.setUser(user);
        user.setClient(client);
        return repository.save(client);
    }

    //obtener todos los clientes
//    public Page<ClientsListDto> findAllClients(Pageable pagination) {
//        return repository.findByActiveTrue(pagination).map(ClientsListDto::new);
//    }

    //obtener propio perfil
    public ClientDetailsDto getOwnClientProfile(User authenticatedUser) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new ValidacionException("El usuario no tiene cliente asociado");
        }
        return new ClientDetailsDto(client);
    }

    //obtener todos los prestamos del cliente por dni
    public Page<LoansListDto> findAllLoansByAuthenticatedClient(User authenticatedUser, Pageable pagination) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new ValidacionException("El usuario no tiene cliente asociado");
        }
        Page<Loan> loans = loanRepository.findByClientPersonaDni(client.getPersona().getDni(), pagination);
        return loans.map(LoansListDto::new);
    }

    //obtener todas las cuentas del cliente por dni
    public Page<AccountDto> findAllAccountsByAuthenticatedClient(User authenticatedUser, Pageable pagination) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new ValidacionException("El usuario no tiene cliente asociado");
        }
        Page<Account> accounts = accountRepository.findAccountsByClientPersonaDniAndActiveTrue(
                client.getPersona().getDni(), pagination);
        return accounts.map(AccountDto::new);
    }

    //update cliente
    public ClientDetailsDto updateOwnClientDetails(User authenticatedUser, String telefono, String email) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new ValidacionException("El usuario no tiene un cliente asociado");
        }
        if (telefono != null && !telefono.isBlank()) {
            client.getPersona().setTelefono(telefono);
        }
        if (email != null && !email.isBlank()) {
            client.getPersona().setEmail(email);
        }
        repository.save(client);
        return new ClientDetailsDto(client);
    }

    //desactivar cliente
    public void deactivateOwnClient(User authenticatedUser) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new ClientNoExisteException("El usuario no tiene un cliente asociado");
        }
        client.deactivate();
        repository.save(client);
    }


}
