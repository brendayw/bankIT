package ar.edu.utn.frbb.tup.integration.repository;

import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.payment.Payment;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private TestEntityManager em;

    private Loan loan;

    @BeforeEach
    void setUp() {
        Person person = Person.builder()
                .dni(12345678L)
                .nombre("NombreTest")
                .apellido("ApellidoTest")
                .fechaNacimiento(LocalDate.now().minusYears(30))
                .build();

        Client client = Client.builder()
                .person(person)
                .registrationDate(LocalDate.now())
                .build();
        em.persist(client);

        loan = Loan.builder()
                .client(client)
                .requestedAmount(1000.0)
                .registrationDate(LocalDate.now())
                .termInMonths(12)
                .build();
        em.persist(loan);

        Payment payment1 = Payment.builder()
                .paymentNumber(1)
                .paymentAmount(100.0)
                .paid(false)
                .paymentDate(LocalDate.now())
                .loan(loan)
                .build();

        Payment payment2 = Payment.builder()
                .paymentNumber(2)
                .paymentAmount(100.0)
                .paid(false)
                .paymentDate(LocalDate.now().plusMonths(1))
                .loan(loan)
                .build();

        em.persist(payment1);
        em.persist(payment2);

        em.flush();
    }

    @Test
    void givenExistingPayments_whenFindingByLoanId_thenReturnPayments() {
        List<Payment> payments = repository.findByLoanId(loan.getId());

        assertThat(payments).hasSize(2);
        assertThat(payments).allMatch(p -> p.getLoan().getId().equals(loan.getId()));
    }

    @Test
    void givenNoPayments_whenFindingByLoanId_thenReturnEmptyList() {
        List<Payment> payments = repository.findByLoanId(9999L);

        assertThat(payments).isEmpty();
    }

    @Test
    void givenExistingPayment_whenSaving_thenItIsPersisted() {
        Payment newPayment = Payment.builder()
                .paymentNumber(3)
                .paymentAmount(200.0)
                .paid(false)
                .paymentDate(LocalDate.now().plusMonths(2))
                .loan(loan)
                .build();

        Payment saved = repository.save(newPayment);

        assertThat(saved.getId()).isNotNull();
        List<Payment> payments = repository.findByLoanId(loan.getId());
        assertThat(payments).contains(saved);
    }
}
