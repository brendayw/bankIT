package ar.edu.utn.frbb.tup.integration.repository;

import ar.edu.utn.frbb.tup.model.payment.PaymentPlan;
import ar.edu.utn.frbb.tup.repository.PaymentPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PaymentPlanRepositoryTest {

    @Autowired
    private PaymentPlanRepository repository;

    @Autowired
    private TestEntityManager em;

    private PaymentPlan paymentPlan;

    @BeforeEach
    void setUp() {
        paymentPlan = PaymentPlan.builder()
                .installments(12)
                .interestRate(10.0)
                .fixedAmount(true)
                .build();

        em.persist(paymentPlan);
        em.flush();
    }

    @Test
    void givenPaymentPlan_whenSaving_thenItIsPersisted() {
        PaymentPlan newPlan = PaymentPlan.builder()
                .installments(6)
                .interestRate(8.0)
                .fixedAmount(false)
                .build();
        PaymentPlan savedPlan = repository.save(newPlan);

        assertThat(savedPlan.getId()).isNotNull();
        assertThat(repository.findById(savedPlan.getId())).isPresent();
    }

    @Test
    void givenExistingPaymentPlan_whenFindingById_thenReturnPlan() {
        Optional<PaymentPlan> found = repository.findById(paymentPlan.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getInstallments()).isEqualTo(12);
        assertThat(found.get().getInterestRate()).isEqualTo(10.0);
        assertThat(found.get().getFixedAmount()).isTrue();
    }

    @Test
    void givenExistingPaymentPlan_whenDeleting_thenItIsRemoved() {
        repository.delete(paymentPlan);

        Optional<PaymentPlan> found = repository.findById(paymentPlan.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void givenNoPaymentPlan_whenFindingById_thenReturnEmpty() {
        Optional<PaymentPlan> found = repository.findById(9999L);

        assertThat(found).isEmpty();
    }
}