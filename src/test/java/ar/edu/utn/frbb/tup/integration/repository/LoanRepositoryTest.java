package ar.edu.utn.frbb.tup.integration.repository;

import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
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
public class LoanRepositoryTest {
    @Autowired
    private LoanRepository repository;

    @Autowired
    private TestEntityManager em;

    private Person person;
    private Client client;

    @BeforeEach
    void setUp() {
        person = Person.builder()
                .dni(12345678L)
                .nombre("NombreTest")
                .apellido("ApellidoTest")
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
    void givenExistingLoans_whenFindingByClientDni_thenReturnLoans() {
        Loan loan1 = createLoan(1000.0);
        Loan loan2 = createLoan(2000.0);
        em.persist(loan1);
        em.persist(loan2);
        em.flush();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Loan> result = repository.findByClientPersonDni(12345678L, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .allMatch(loan -> loan.getClient().getPerson().getDni().equals(12345678L));
    }

    @Test
    void givenNoLoans_whenFindingByClientDni_thenReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Loan> result = repository.findByClientPersonDni(12345678L, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    // Helper
    private Loan createLoan(Double amount) {
        Loan loan = Loan.builder()
                .client(client)
                .requestedAmount(amount)
                .registrationDate(LocalDate.now())
                .termInMonths(12)
                .build();
        return loan;
    }
}