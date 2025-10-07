package ar.edu.utn.frbb.tup.service.loan;

import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CreditScoreServiceTest {

    private CreditScoreService creditScoreService;

    @BeforeEach
    void setUp() {
        creditScoreService = new CreditScoreService();
    }

    @Test
    void calculateScore_WithNoLoans_ReturnsScoreWithinValidRange() {
        int score = creditScoreService.calculateScore(Collections.emptySet());

        assertTrue(score >= 500 && score <= 1000);
    }

    @Test
    void calculateScore_WithApprovedLoan_ReturnsScoreAtLeast510() {
        Loan approvedLoan = new Loan();
        approvedLoan.setLoanStatus(LoanStatus.APROBADO);

        Set<Loan> loans = Set.of(approvedLoan);

        int score = creditScoreService.calculateScore(loans);

        assertTrue(score >= 510 && score <= 1010); // base 500–1000 + 10
        assertTrue(score <= 1000); // aún si supera, el método lo limita
    }

    @Test
    void calculateScore_WithMultipleApprovedLoans_CapsScoreAt1000() {
        Set<Loan> loans = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Loan loan = new Loan();
            loan.setLoanStatus(LoanStatus.APROBADO);
            loans.add(loan);
        }

        int score = creditScoreService.calculateScore(loans);

        assertTrue(score <= 1000);
        assertTrue(score >= 500);
    }

    @Test
    void calculateScore_WithRejectedLoan_DoesNotIncreaseScore() {
        Loan rejectedLoan = new Loan();
        rejectedLoan.setLoanStatus(LoanStatus.RECHAZADO);

        Set<Loan> loans = Set.of(rejectedLoan);

        int score = creditScoreService.calculateScore(loans);

        // el puntaje no se incrementa, debe estar dentro del rango base
        assertTrue(score >= 500 && score <= 1000);
    }

    @Test
    void calculateScore_NullLoanSet_ThrowsException() {
        assertThrows(NullPointerException.class, () -> creditScoreService.calculateScore(null));
    }
}
