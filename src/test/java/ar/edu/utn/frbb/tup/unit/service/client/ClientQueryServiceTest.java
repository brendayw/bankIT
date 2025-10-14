package ar.edu.utn.frbb.tup.unit.service.client;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDetailsDto;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.dto.LoansListDto;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.AccountRepository;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import ar.edu.utn.frbb.tup.service.client.ClientQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientQueryServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private ClientQueryService clientQueryService;

    private User createUserWithClient() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .build();

        Client client = Client.builder()
                .id(1L)
                .person(Person.builder()
                        .dni(12345678L)
                        .apellido("Pérez")
                        .nombre("Juan")
                        .fechaNacimiento(LocalDate.of(1990, 1, 1))
                        .telefono("123456789")
                        .email("juan@email.com")
                        .build())
                .active(true)
                .build();

        user.associateWithClient(client);
        return user;
    }

    private User createUserWithoutClient() {
        return User.builder()
                .id(2L)
                .username("userwithoutclient")
                .password("encodedPassword")
                .build();
    }

    private Loan createSampleLoan(Long id, Double amount, LoanStatus status) {
        return Loan.builder()
                .id(id)
                .requestedAmount(amount)
                .loanStatus(status)
                .registrationDate(LocalDate.now())
                .build();
    }

    private Account createSampleAccount(Long id, Double balance, AccountType accountType) {
        return Account.builder()
                .id(id)
                .balance(balance)
                .accountType(accountType)
                .currencyType(CurrencyType.PESOS)
                .active(true)
                .build();
    }

    @Test
    void getOwnClientProfileWithValidClientReturnsClientDetails() {
        User user = createUserWithClient();
        Client client = user.getClient();
        ClientDetailsDto result = clientQueryService.getOwnClientProfile(user);

        assertThat(result).isNotNull();
        assertThat(result.dni()).isEqualTo(12345678L);
        assertThat(result.apellido()).isEqualTo("Pérez");
        assertThat(result.nombre()).isEqualTo("Juan");
    }

    @Test
    void getOwnClientProfileWithUserWithoutClientThrowsValidationException() {
        User user = createUserWithoutClient();

        assertThrows(ValidationException.class, () -> {
            clientQueryService.getOwnClientProfile(user);
        });
    }

    @Test
    void getOwnClientProfileWithNullUserThrowsException() {
        User nullUser = null;

        assertThrows(NullPointerException.class, () -> {
            clientQueryService.getOwnClientProfile(nullUser);
        });
    }

    @Test
    void findAllLoansByAuthenticatedClientWithValidClientReturnsLoansPage() {
        User user = createUserWithClient();
        Client client = user.getClient();

        Loan loan1 = createSampleLoan(1L, 50000.0, LoanStatus.APROBADO);
        loan1.setClient(client);
        Loan loan2 = createSampleLoan(2L, 30000.0, LoanStatus.APROBADO);
        loan2.setClient(client);

        List<Loan> loans = List.of(loan1, loan2);
        Page<Loan> loanPage = new PageImpl<>(loans);
        Pageable pageable = PageRequest.of(0, 10);

        when(loanRepository.findByClientPersonDni(eq(12345678L), eq(pageable)))
                .thenReturn(loanPage);

        Page<LoansListDto> result = clientQueryService.findAllLoansByAuthenticatedClient(user, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).id()).isEqualTo(1L);
        assertThat(result.getContent().get(1).id()).isEqualTo(2L);
        verify(loanRepository).findByClientPersonDni(12345678L, pageable);
    }

    @Test
    void findAllLoansByAuthenticatedClientWithEmptyLoansReturnsEmptyPage() {
        User user = createUserWithClient();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Loan> emptyPage = new PageImpl<>(List.of());

        when(loanRepository.findByClientPersonDni(eq(12345678L), eq(pageable)))
                .thenReturn(emptyPage);

        Page<LoansListDto> result = clientQueryService.findAllLoansByAuthenticatedClient(user, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(loanRepository).findByClientPersonDni(12345678L, pageable);
    }

    @Test
    void findAllLoansByAuthenticatedClientWithUserWithoutClientThrowsValidationException() {
        User user = createUserWithoutClient();
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(ValidationException.class, () -> {
            clientQueryService.findAllLoansByAuthenticatedClient(user, pageable);
        });
    }

    @Test
    void findAllLoansByAuthenticatedClientWithDifferentPaginationWorksCorrectly() {
        User user = createUserWithClient();
        Client client = user.getClient();
        Pageable pageable = PageRequest.of(1, 5);

        Loan loan = createSampleLoan(1L, 10000.0, LoanStatus.APROBADO);
        loan.setClient(client);

        Page<Loan> loanPage = new PageImpl<>(List.of(loan));

        when(loanRepository.findByClientPersonDni(eq(12345678L), eq(pageable)))
                .thenReturn(loanPage);

        Page<LoansListDto> result = clientQueryService.findAllLoansByAuthenticatedClient(user, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(loanRepository).findByClientPersonDni(12345678L, pageable);
    }

    @Test
    void findAllAccountsByAuthenticatedClientWithValidClientReturnsAccountsPage() {
        User user = createUserWithClient();
        Client client = user.getClient();

        Account account1 = createSampleAccount(1L, 1000.0, AccountType.CAJA_AHORRO);
        account1.setClient(client);
        Account account2 = createSampleAccount(2L, 5000.0, AccountType.CUENTA_CORRIENTE);
        account2.setClient(client);

        List<Account> accounts = List.of(account1, account2);
        Page<Account> accountPage = new PageImpl<>(accounts);
        Pageable pageable = PageRequest.of(0, 10);

        when(accountRepository.findAccountsByClientPersonDniAndActiveTrue(eq(12345678L), eq(pageable)))
                .thenReturn(accountPage);

        Page<AccountDto> result = clientQueryService.findAllAccountsByAuthenticatedClient(user, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        assertThat(result.getContent().get(0).dni()).isEqualTo(12345678L);
        assertThat(result.getContent().get(0).balance()).isEqualTo(1000.0);
        assertThat(result.getContent().get(0).accountType()).isEqualTo(AccountType.CAJA_AHORRO);

        assertThat(result.getContent().get(1).dni()).isEqualTo(12345678L);
        assertThat(result.getContent().get(1).balance()).isEqualTo(5000.0);
        assertThat(result.getContent().get(1).accountType()).isEqualTo(AccountType.CUENTA_CORRIENTE);

        verify(accountRepository).findAccountsByClientPersonDniAndActiveTrue(12345678L, pageable);
    }

    @Test
    void findAllAccountsByAuthenticatedClientWithOnlyActiveAccountsReturnsFilteredResults() {
        User user = createUserWithClient();
        Client client = user.getClient();
        Pageable pageable = PageRequest.of(0, 10);

        Account activeAccount = createSampleAccount(1L, 1000.0, AccountType.CAJA_AHORRO);
        activeAccount.setClient(client);

        List<Account> activeAccounts = List.of(activeAccount);
        Page<Account> accountPage = new PageImpl<>(activeAccounts);

        when(accountRepository.findAccountsByClientPersonDniAndActiveTrue(eq(12345678L), eq(pageable)))
                .thenReturn(accountPage);

        Page<AccountDto> result = clientQueryService.findAllAccountsByAuthenticatedClient(user, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).dni()).isEqualTo(12345678L);
        verify(accountRepository).findAccountsByClientPersonDniAndActiveTrue(12345678L, pageable);
    }

    @Test
    void findAllAccountsByAuthenticatedClientWithEmptyAccountsReturnsEmptyPage() {
        User user = createUserWithClient();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> emptyPage = new PageImpl<>(List.of());

        when(accountRepository.findAccountsByClientPersonDniAndActiveTrue(eq(12345678L), eq(pageable)))
                .thenReturn(emptyPage);

        Page<AccountDto> result = clientQueryService.findAllAccountsByAuthenticatedClient(user, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(accountRepository).findAccountsByClientPersonDniAndActiveTrue(12345678L, pageable);
    }

    @Test
    void findAllAccountsByAuthenticatedClientWithUserWithoutClientThrowsValidationException() {
        User user = createUserWithoutClient();
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(ValidationException.class, () -> {
            clientQueryService.findAllAccountsByAuthenticatedClient(user, pageable);
        });
    }

    @Test
    void findAllAccountsByAuthenticatedClientWithNullPageableUsesDefault() {
        User user = createUserWithClient();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> accountPage = new PageImpl<>(List.of(createSampleAccount(1L, 1000.0, AccountType.CAJA_AHORRO)));

        when(accountRepository.findAccountsByClientPersonDniAndActiveTrue(eq(12345678L), eq(pageable)))
                .thenReturn(accountPage);

        Page<AccountDto> result = clientQueryService.findAllAccountsByAuthenticatedClient(user, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void multipleQueryMethodsWithSameUserWorkIndependently() {
        User user = createUserWithClient();
        Client client = user.getClient();
        Pageable pageable = PageRequest.of(0, 10);

        Loan loan = createSampleLoan(1L, 10000.0, LoanStatus.APROBADO);
        loan.setClient(client);
        Account account = createSampleAccount(1L, 1000.0, AccountType.CAJA_AHORRO);
        account.setClient(client);

        Page<Loan> loanPage = new PageImpl<>(List.of(loan));
        Page<Account> accountPage = new PageImpl<>(List.of(account));

        when(loanRepository.findByClientPersonDni(eq(12345678L), eq(pageable))).thenReturn(loanPage);
        when(accountRepository.findAccountsByClientPersonDniAndActiveTrue(eq(12345678L), eq(pageable))).thenReturn(accountPage);

        ClientDetailsDto profile = clientQueryService.getOwnClientProfile(user);
        Page<LoansListDto> loans = clientQueryService.findAllLoansByAuthenticatedClient(user, pageable);
        Page<AccountDto> accounts = clientQueryService.findAllAccountsByAuthenticatedClient(user, pageable);

        assertThat(profile).isNotNull();
        assertThat(loans.getContent()).hasSize(1);
        assertThat(accounts.getContent()).hasSize(1);

        verify(loanRepository).findByClientPersonDni(12345678L, pageable);
        verify(accountRepository).findAccountsByClientPersonDniAndActiveTrue(12345678L, pageable);
    }
}
