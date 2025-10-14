package ar.edu.utn.frbb.tup.integration.repository;

import ar.edu.utn.frbb.tup.config.IntegrationTestBase;
import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private TestEntityManager em;

    private Person person;
    private Client client;

    @BeforeEach
    void setUp() {
        this.client = createClient(12345678L);
        this.person = this.client.getPerson();
    }

    @Test
    void givenExistingAccount_whenCheckingExistence_theReturnTrue() {
        Account account = createAccount(client, AccountType.CUENTA_CORRIENTE, CurrencyType.PESOS, true);
        em.persist(account);

        boolean exists = repository.existsByClientPersonDniAndAccountTypeAndCurrencyType(
                12345678L, AccountType.CUENTA_CORRIENTE, CurrencyType.PESOS);

        assertThat(exists).isTrue();
    }

    @Test
    void givenNoMatchingAccount_whenCheckingExistence_thenReturnFalse() {

        boolean exists = repository.existsByClientPersonDniAndAccountTypeAndCurrencyType(
                99999999L, AccountType.CAJA_AHORRO, CurrencyType.DOLARES);

        assertThat(exists).isFalse();
    }

    @Test
    void givenActiveAndInactiveAccounts_whenFindingActiveAccounts_thenReturnOnlyActiveAccounts() {
        Account activeAccount = createAccount(client, AccountType.CAJA_AHORRO, CurrencyType.PESOS, true);
        Account inactiveAccount = createAccount(client, AccountType.CAJA_AHORRO, CurrencyType.DOLARES, false);

        em.persist(activeAccount);
        em.persist(inactiveAccount);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Account> result = repository.findAccountsByClientPersonDniAndActiveTrue(12345678L, pageable);

        assertThat(result.getContent()).hasSize(2); // Solo las 2 activas
    }

    @Test
    void givenOnlyInactiveAccounts_whenFindingActiveAccounts_thenReturnEmptyPage() {
        Account inactiveAccount = createAccount(client, AccountType.CAJA_AHORRO, CurrencyType.PESOS, false);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> result = repository.findAccountsByClientPersonDniAndActiveTrue(12345678L, pageable);

        assertThat(result.getContent()).isEmpty();
    }


    @Test
    void givenMultipleActiveAccounts_whenPaginating_thenReturnCorrectNumberOfElementsPerPage() {
        for (int i = 0; i < 15; i++) {
            Account account = createAccount(client, AccountType.CAJA_AHORRO, CurrencyType.PESOS, true);
            em.persist(account);
        }
        Pageable firstPage = PageRequest.of(0, 5);
        Pageable secondPage = PageRequest.of(1, 5);

        Page<Account> firstPageResult = repository.findAccountsByClientPersonDniAndActiveTrue(12345678L, firstPage);
        Page<Account> secondPageResult = repository.findAccountsByClientPersonDniAndActiveTrue(12345678L, secondPage);

        assertThat(firstPageResult.getContent()).hasSize(5);
        assertThat(secondPageResult.getContent()).hasSize(5);
        assertThat(firstPageResult.getTotalElements()).isEqualTo(15);
        assertThat(firstPageResult.getTotalPages()).isEqualTo(3);
    }

    //HELPERS
    private Client createClient(Long dni) {
        Person person = Person.builder()
                .dni(12345678L)
                .nombre("NombreTest")
                .apellido("ApellidoTest")
                .email("testuse@email.com")
                .telefono("2915748896")
                .fechaNacimiento(LocalDate.now().minusYears(30))
                .build();

        Client client = Client.builder()
                .person(person)
                .registrationDate(LocalDate.now())
                .build();
        em.persist(client);
        return client;
    }

    private Account createAccount(Client client, AccountType accountType, CurrencyType currencyType, boolean active) {
        Account account = Account.builder()
                .client(client)
                .accountType(accountType)
                .currencyType(currencyType)
                .balance(Double.valueOf(0.0))
                .active(true)
                .creationDate(LocalDate.now())
                .build();

        return account;
    }
}