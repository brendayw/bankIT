package ar.edu.utn.frbb.tup.service.loan;

import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.loan.exceptions.CreditScoreException;
import ar.edu.utn.frbb.tup.model.person.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanSimulatorServiceTest {

    @Mock
    private CreditScoreService creditScoreService;

    @InjectMocks
    private LoanSimulatorService loanSimulatorService;

    private Client client;
    private LoanRequestDto loanRequestDto;
    private Person person;

    private final Double REQUESTED_AMOUNT = 100000.0;
    private final Integer TERM_IN_MONTHS = 12;
    private final Long CLIENT_DNI = 12345678L;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setDni(CLIENT_DNI);
        client = new Client();
        client.setPerson(person);
        loanRequestDto = new LoanRequestDto(
                CLIENT_DNI,
                REQUESTED_AMOUNT,
                CurrencyType.PESOS,
                AccountType.CAJA_AHORRO,
                TERM_IN_MONTHS
        );
    }

    @Test
    void createLoan_ApprovedLoan_Success() throws CreditScoreException {
        int highScore = 600; // Mayor que MIN_APPROVAL_SCORE (500)
        when(creditScoreService.calculateScore(any())).thenReturn(highScore);

        Double expectedInterest = REQUESTED_AMOUNT * (0.40 / 12) * TERM_IN_MONTHS;
        Double expectedTotalAmount = REQUESTED_AMOUNT + expectedInterest;

        Loan result = loanSimulatorService.createLoan(client, loanRequestDto);

        assertNotNull(result);
        assertEquals(LoanStatus.APROBADO, result.getLoanStatus());
        assertEquals(REQUESTED_AMOUNT, result.getRequestedAmount());
        assertEquals(TERM_IN_MONTHS, result.getTermInMonths());
        assertEquals(expectedInterest, result.getInteres(), 0.01);
        assertEquals(expectedTotalAmount, result.getTotalAmount(), 0.01);
        assertNotNull(result.getRegistrationDate());
        assertNotNull(result.getCuotas());
        assertNull(result.getPaymentPlan());

        verify(creditScoreService, times(1)).calculateScore(any());
    }

    @Test
    void createLoan_RejectedLoan_Success() throws CreditScoreException {
        int lowScore = 400; // Menor que MIN_APPROVAL_SCORE (500)
        when(creditScoreService.calculateScore(any())).thenReturn(lowScore);

        Loan result = loanSimulatorService.createLoan(client, loanRequestDto);

        assertNotNull(result);
        assertEquals(LoanStatus.RECHAZADO, result.getLoanStatus());
        assertEquals(REQUESTED_AMOUNT, result.getRequestedAmount());
        assertEquals(TERM_IN_MONTHS, result.getTermInMonths());
        assertEquals(0.0, result.getInteres());
        assertEquals(0.0, result.getTotalAmount());
        assertNotNull(result.getRegistrationDate());
        assertNotNull(result.getCuotas());
        assertNull(result.getPaymentPlan());

        verify(creditScoreService, times(1)).calculateScore(any());
    }

    @Test
    void createLoan_ExactMinimumScore_Approved() throws CreditScoreException {
        int exactScore = 500; // Exactamente el mínimo para aprobar
        when(creditScoreService.calculateScore(any())).thenReturn(exactScore);

        Loan result = loanSimulatorService.createLoan(client, loanRequestDto);

        assertEquals(LoanStatus.APROBADO, result.getLoanStatus());
        assertTrue(result.getTotalAmount() > REQUESTED_AMOUNT);
        verify(creditScoreService, times(1)).calculateScore(any());
    }

    @Test
    void createLoan_CreditScoreServiceThrowsException_PropagatesException() throws CreditScoreException {
        CreditScoreException expectedException = new CreditScoreException("Error calculando score");
        when(creditScoreService.calculateScore(any())).thenThrow(expectedException);

        CreditScoreException exception = assertThrows(CreditScoreException.class, () ->
                loanSimulatorService.createLoan(client, loanRequestDto)
        );

        assertEquals(expectedException, exception);
        verify(creditScoreService, times(1)).calculateScore(any());
    }

    @Test
    void createLoan_WithExistingLoans_Success() throws CreditScoreException {
        Loan existingLoan = new Loan();
        client.getLoans().add(existingLoan);

        int highScore = 600;
        when(creditScoreService.calculateScore(any())).thenReturn(highScore);

        Loan result = loanSimulatorService.createLoan(client, loanRequestDto);

        assertNotNull(result);
        assertEquals(LoanStatus.APROBADO, result.getLoanStatus());
        verify(creditScoreService, times(1)).calculateScore(any(HashSet.class));
    }

    @Test
    void createLoan_ZeroAmount_ApprovedWithInterest() throws CreditScoreException {
        LoanRequestDto zeroAmountRequest = new LoanRequestDto(CLIENT_DNI, 0.0, CurrencyType.PESOS,
                AccountType.CAJA_AHORRO, TERM_IN_MONTHS
        );
        int highScore = 600;
        when(creditScoreService.calculateScore(any())).thenReturn(highScore);

        Loan result = loanSimulatorService.createLoan(client, zeroAmountRequest);

        assertEquals(LoanStatus.APROBADO, result.getLoanStatus());
        assertEquals(0.0, result.getInteres());
        assertEquals(0.0, result.getTotalAmount());
    }

    @Test
    void createLoan_OneMonthTerm_CalculatesInterestCorrectly() throws CreditScoreException {
        LoanRequestDto oneMonthRequest = new LoanRequestDto(CLIENT_DNI, REQUESTED_AMOUNT, CurrencyType.PESOS,
                AccountType.CAJA_AHORRO, 1
        );
        int highScore = 600;
        when(creditScoreService.calculateScore(any())).thenReturn(highScore);

        Double expectedInterest = REQUESTED_AMOUNT * (0.40 / 12) * 1;

        Loan result = loanSimulatorService.createLoan(client, oneMonthRequest);

        assertEquals(LoanStatus.APROBADO, result.getLoanStatus());
        assertEquals(expectedInterest, result.getInteres(), 0.01);
        assertEquals(REQUESTED_AMOUNT + expectedInterest, result.getTotalAmount(), 0.01);
    }

    @Test
    void createLoan_NullClient_ThrowsException() {
        assertThrows(NullPointerException.class, () ->
                loanSimulatorService.createLoan(null, loanRequestDto)
        );
    }

    @Test
    void createLoan_NullLoanRequest_ThrowsException() {
        assertThrows(NullPointerException.class, () ->
                loanSimulatorService.createLoan(client, null)
        );
    }

    @Test
    void calculateRate_StandardCase_ReturnsCorrectInterest() {
        Double amount = 100000.0;
        Integer months = 12;
        Double expectedInterest = amount * (0.40 / 12) * months;
        Double result = loanSimulatorService.calculateRate(amount, months);

        assertEquals(expectedInterest, result, 0.01);
    }

    @Test
    void calculateRate_ZeroAmount_ReturnsZero() {
        Double result = loanSimulatorService.calculateRate(0.0, TERM_IN_MONTHS);

        assertEquals(0.0, result);
    }

    @Test
    void calculateRate_ZeroMonths_ReturnsZero() {
        Double result = loanSimulatorService.calculateRate(REQUESTED_AMOUNT, 0);

        assertEquals(0.0, result);
    }

    @Test
    void calculateRate_NullAmount_ThrowsException() {
        assertThrows(NullPointerException.class, () ->
                loanSimulatorService.calculateRate(null, TERM_IN_MONTHS)
        );
    }

    @Test
    void calculateRate_NullMonths_ThrowsException() {
        assertThrows(NullPointerException.class, () ->
                loanSimulatorService.calculateRate(REQUESTED_AMOUNT, null)
        );
    }

    @Test
    void calculateRate_LargeAmountAndTerm_ReturnsCorrectInterest() {
        Double largeAmount = 1000000.0;
        Integer longTerm = 60; // 5 años
        Double expectedInterest = largeAmount * (0.40 / 12) * longTerm;
        Double result = loanSimulatorService.calculateRate(largeAmount, longTerm);

        assertEquals(expectedInterest, result, 0.01);
    }

    @Test
    void createLoan_DifferentCurrencyAndAccountType_Success() throws CreditScoreException {
        LoanRequestDto differentRequest = new LoanRequestDto(CLIENT_DNI, REQUESTED_AMOUNT, CurrencyType.DOLARES,
                AccountType.CAJA_AHORRO, TERM_IN_MONTHS
        );
        int highScore = 600;
        when(creditScoreService.calculateScore(any())).thenReturn(highScore);

        Loan result = loanSimulatorService.createLoan(client, differentRequest);

        assertNotNull(result);
        assertEquals(CurrencyType.DOLARES, result.getCurrencyType());
        assertEquals(AccountType.CAJA_AHORRO, result.getAccountType());
        assertEquals(LoanStatus.APROBADO, result.getLoanStatus());
    }
}
