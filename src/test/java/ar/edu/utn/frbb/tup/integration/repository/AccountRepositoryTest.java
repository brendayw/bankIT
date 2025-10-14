package ar.edu.utn.frbb.tup.integration.repository;

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
        person = Person.builder()
                .dni(12345678L)
                .nombre("Juan")
                .apellido("Perez")
                .fechaNacimiento(LocalDate.now().minusYears(30))
                .build();

        client = Client.builder()
                .person(person)
                .registrationDate(LocalDate.now())
                .build();
        em.persist(client);

        em.flush();
    }

    @Test
    void givenExistingAccount_whenCheckingExistence_theReturnTrue() {
        Account account = createAccount(12345678L, AccountType.CUENTA_CORRIENTE, CurrencyType.PESOS, true);
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
        Account activeAccount1 = createAccount(12345678L, AccountType.CAJA_AHORRO, CurrencyType.PESOS, true);
        Account activeAccount2 = createAccount(12345678L, AccountType.CUENTA_CORRIENTE, CurrencyType.DOLARES, true);
        Account inactiveAccount = createAccount(12345678L, AccountType.CAJA_AHORRO, CurrencyType.DOLARES, false);
        em.persist(activeAccount1);
        em.persist(activeAccount2);
        em.persist(inactiveAccount);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Account> result = repository.findAccountsByClientPersonDniAndActiveTrue(12345678L, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .allMatch(Account::isActive);
    }

    @Test
    void givenOnlyInactiveAccounts_whenFindingActiveAccounts_thenReturnEmptyPage() {
        Account inactiveAccount = createAccount(12345678L, AccountType.CAJA_AHORRO, CurrencyType.PESOS, false);
        em.persist(inactiveAccount);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Account> result = repository.findAccountsByClientPersonDniAndActiveTrue(12345678L, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void givenMultipleActiveAccounts_whenPaginating_thenReturnCorrectNumberOfElementsPerPage() {
        for (int i = 0; i < 15; i++) {
            Account account = createAccount(12345678L, AccountType.CAJA_AHORRO, CurrencyType.PESOS, true);
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

    // Helper
    private Account createAccount(Long dni, AccountType accountType, CurrencyType currencyType, boolean active) {
        Person accountPerson = person;
        Client accountClient = client;

        if (!dni.equals(person.getDni())) {
            accountPerson = new Person();
            accountPerson.setDni(dni);
            accountPerson.setNombre("Test");
            accountPerson.setApellido("User");
            accountPerson.setFechaNacimiento(LocalDate.now().minusYears(25));
            em.persist(accountPerson);

            accountClient = new Client();
            accountClient.setPerson(accountPerson);
            accountClient.setRegistrationDate(LocalDate.now());
            em.persist(accountClient);
        }

        Account account = new Account();
        account.setClient(accountClient);
        account.setAccountType(accountType);
        account.setCurrencyType(currencyType);
        account.setBalance(Double.valueOf(0.0));
        account.setActive(active);
        account.setCreationDate(LocalDate.now());

        return account;
    }
}