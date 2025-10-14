package ar.edu.utn.frbb.tup.unit.controller;

import ar.edu.utn.frbb.tup.controller.ClientController;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDetailsDto;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDto;
import ar.edu.utn.frbb.tup.model.client.dto.UpdateClientDto;
import ar.edu.utn.frbb.tup.model.loan.dto.LoansListDto;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.model.person.dto.PersonDto;
import ar.edu.utn.frbb.tup.model.person.enums.PersonType;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.service.client.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ClientControllerTest {

    @Mock
    private ClientCreationService creationService;

    @Mock
    private ClientQueryService queryService;

    @Mock
    private ClientUpdateService updateService;

    @Mock
    private ClientManagementService managementService;

    @InjectMocks
    private ClientController clientController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Usuario mockeado
        user = new User();
        user.setId(1L);
        user.setUsername("userTest");

        // Mock SecurityContext
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        var securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldRegisterClientSuccessfully() {
        ClientDto dto = createClientDto("email@test.com");
        Client client = createClientWithPerson("email@test.com");

        when(creationService.createClient(dto, user)).thenReturn(client);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

        ResponseEntity<?> response = clientController.register(dto, uriBuilder);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isInstanceOf(ClientDetailsDto.class);
        verify(creationService).createClient(dto, user);
    }

    @Test
    void shouldGetClientProfile() {
        ClientDetailsDto clientDetails = createClientDetailsDto(1L, "email@test.com");
        when(queryService.getOwnClientProfile(user)).thenReturn(clientDetails);

        ResponseEntity<ClientDetailsDto> response = clientController.getClientProfile(user);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(clientDetails);
        verify(queryService).getOwnClientProfile(user);
    }

    @Test
    void shouldGetAllLoansByClient() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<LoansListDto> loansPage = new PageImpl<>(List.of(
                new LoansListDto(1L, 12345678L, 1000.0, CurrencyType.PESOS, 12, LoanStatus.APROBADO),
                new LoansListDto(2L, 12345678L, 1500.0, CurrencyType.PESOS, 12, LoanStatus.APROBADO)
        ));
        when(queryService.findAllLoansByAuthenticatedClient(user, pageable)).thenReturn(loansPage);

        ResponseEntity<?> response = clientController.getAllLoansByClient(user, pageable);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(loansPage);
        verify(queryService).findAllLoansByAuthenticatedClient(user, pageable);
    }

    @Test
    void shouldGetAllAccountsByClient() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<AccountDto> accountsPage = new PageImpl<>(List.of(createAccountDto(1000.0), createAccountDto(2000.0)));
        when(queryService.findAllAccountsByAuthenticatedClient(user, pageable)).thenReturn(accountsPage);

        ResponseEntity<?> response = clientController.getAllAccountsByClient(user, pageable);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(accountsPage);
        verify(queryService).findAllAccountsByAuthenticatedClient(user, pageable);
    }

    @Test
    void shouldUpdateClient() {
        UpdateClientDto updateDto = new UpdateClientDto("1234567890", "newemail@test.com");
        ClientDetailsDto updatedClient = createClientDetailsDto(1L, "newemail@test.com");
        when(updateService.updateOwnClientDetails(user, updateDto.telefono(), updateDto.email())).thenReturn(updatedClient);

        ResponseEntity<?> response = clientController.updateClient(user, updateDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(updatedClient);
        verify(updateService).updateOwnClientDetails(user, updateDto.telefono(), updateDto.email());
    }

    @Test
    void shouldDeactivateClient() {
        ResponseEntity<?> response = clientController.delete(user);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(managementService).deactivateOwnClient(user);
    }

    // =======================
    // Helpers refactorizados
    // =======================

    private Client createClientWithPerson(String email) {
        Person person = new Person();
        person.setDni(12345678L);
        person.setNombre("User");
        person.setApellido("Test");
        person.setFechaNacimiento(LocalDate.of(2002, 9, 10));
        person.setTelefono("2915789631");
        person.setEmail(email);

        Client client = new Client();
        client.setId(1L);
        client.setPerson(person);
        client.setPersonType(PersonType.PERSONA_FISICA);
        client.setRegistrationDate(LocalDate.now());
        return client;
    }

    private ClientDetailsDto createClientDetailsDto(Long id, String email) {
        return new ClientDetailsDto(
                id,
                12345678L,
                "Test",
                "User",
                "1234567890",
                email,
                PersonType.PERSONA_FISICA,
                LocalDate.now(),
                Set.of(),
                Set.of()
        );
    }

    private PersonDto createPersonDto(String email) {
        return new PersonDto(
                12345678L,
                "Test",
                "User",
                LocalDate.of(2002, 9, 10),
                "2915789631",
                email
        );
    }

    private ClientDto createClientDto(String email) {
        return new ClientDto(createPersonDto(email), PersonType.PERSONA_FISICA);
    }

    private AccountDto createAccountDto(double balance) {
        return new AccountDto(12345678L, CurrencyType.PESOS, AccountType.CUENTA_CORRIENTE, balance);
    }
}