package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanDetailsDto;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.loan.dto.LoansListDto;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.loan.exceptions.CreditScoreException;
import ar.edu.utn.frbb.tup.model.loan.exceptions.LoanNotFoundException;
import ar.edu.utn.frbb.tup.model.payment.dto.UpdatePaymentDto;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.model.person.enums.PersonType;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.service.loan.LoanCreationService;
import ar.edu.utn.frbb.tup.service.loan.LoanPaymentService;
import ar.edu.utn.frbb.tup.service.loan.LoanQueryService;
import ar.edu.utn.frbb.tup.service.loan.LoanStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class LoanControllerTest {
    @Mock
    private LoanCreationService loanCreationService;

    @Mock
    private LoanQueryService loanQueryService;

    @Mock
    private LoanPaymentService loanPaymentService;

    @Mock
    private LoanStatusService loanStatusService;

    @InjectMocks
    private LoanController loanController;

    private User user;

    private static final long TEST_DNI = 12345678L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("userTest");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        var securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldRegisterLoanSuccessfully() throws ClientNotFoundException, CreditScoreException {
        LoanRequestDto dto = createLoanRequestDto();
        Loan loan = createLoan();

        when(loanCreationService.registerLoan(user, dto)).thenReturn(loan);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
        ResponseEntity<?> response = loanController.register(user, dto, uriBuilder);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isInstanceOf(LoanDetailsDto.class);
        verify(loanCreationService).registerLoan(user, dto);
    }

    @Test
    void shouldGetLoanById() {
        LoanDetailsDto dto = createLoanDetailsDto();
        when(loanQueryService.findLoanById(user, 1L)).thenReturn(dto);

        ResponseEntity<?> response = loanController.getLoanById(user, 1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
        verify(loanQueryService).findLoanById(user, 1L);
    }

    @Test
    void shouldGetLoansByClient() throws ClientNotFoundException {
        PageRequest pageable = PageRequest.of(0,10);
        Page<LoansListDto> loansPage = new PageImpl<>(List.of(
                createLoansListDto(1L),
                createLoansListDto(2L)
        ));

        when(loanQueryService.findLoansByClient(user, TEST_DNI, pageable)).thenReturn(loansPage);

        ResponseEntity<?> response = loanController.getLoansByClient(user, TEST_DNI, pageable);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(loansPage);
        verify(loanQueryService).findLoansByClient(user, TEST_DNI, pageable);
    }

    @Test
    void shouldPayInstallment() throws LoanNotFoundException, ClientNotFoundException {
        UpdatePaymentDto dto = createUpdatePaymentDto();

        ResponseEntity<?> response = loanController.payInstallment(user, 1L, dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(loanPaymentService).payInstallment(user, 1L, dto);
    }

    @Test
    void shouldCloseLoan() {
        ResponseEntity<?> response = loanController.close(user, 1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(loanStatusService).closeLoan(user, 1L);
    }

    // ---- Helpers ----
    private Client createClientWithPerson(String email) {
        Person person = new Person();
        person.setDni(TEST_DNI);
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

    private Loan createLoan() {
        Loan loan = Loan.builder()
                .id(1L)
                .client(createClientWithPerson("email@test.com"))
                .requestedAmount(120000.0)
                .currencyType(CurrencyType.PESOS)
                .accountType(AccountType.CUENTA_CORRIENTE)
                .termInMonths(12)
                .build();
        return loan;
    }

    private LoanRequestDto createLoanRequestDto() {
        return new LoanRequestDto(TEST_DNI, 120000.00, CurrencyType.PESOS,
                AccountType.CUENTA_CORRIENTE, 12);
    }

    private LoansListDto createLoansListDto(long id) {
        return new LoansListDto(id, TEST_DNI, 168000.00, CurrencyType.PESOS, 12, LoanStatus.APROBADO);
    }

    private LoanDetailsDto createLoanDetailsDto() {
        return new LoanDetailsDto(createLoan());
    }

    private UpdatePaymentDto createUpdatePaymentDto() {
        return new UpdatePaymentDto(1L, TEST_DNI, LocalDate.now());
    }
}
