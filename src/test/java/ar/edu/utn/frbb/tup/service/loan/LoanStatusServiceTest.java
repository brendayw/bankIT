package ar.edu.utn.frbb.tup.service.loan;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.loan.exceptions.LoanNotFoundException;
import ar.edu.utn.frbb.tup.model.payment.Payment;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanStatusServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanStatusService loanStatusService;

    private User authenticatedUser;
    private User differentUser;
    private Client client;
    private Loan loan;
    private Payment paidPayment;
    private Payment unpaidPayment;
    private final Long LOAN_ID = 1L;

    @BeforeEach
    void setUp() {
        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        differentUser = new User();
        differentUser.setId(2L);
        client = new Client();
        client.setUser(authenticatedUser);

        // Crear cuotas/pagos
        paidPayment = new Payment();
        paidPayment.setPaid(true);
        paidPayment.setPaymentDate(LocalDate.now().minusDays(10));

        unpaidPayment = new Payment();
        unpaidPayment.setPaid(false);
        unpaidPayment.setPaymentDate(null);

        loan = new Loan();
        loan.setId(LOAN_ID);
        loan.setClient(client);
        loan.setCuotas(Arrays.asList(paidPayment, unpaidPayment));
    }

    @Test
    void closeLoan_Success() {
        // Arrange
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        assertDoesNotThrow(() -> loanStatusService.closeLoan(authenticatedUser, LOAN_ID));

        // Assert
        verify(loanRepository, times(1)).findById(LOAN_ID);
        verify(loanRepository, times(1)).save(loan);

        assertEquals(LoanStatus.CERRADO, loan.getLoanStatus());

        assertTrue(unpaidPayment.getPaid());
        assertNotNull(unpaidPayment.getPaymentDate());
        assertEquals(LocalDate.now(), unpaidPayment.getPaymentDate());

        assertTrue(paidPayment.getPaid());
        assertNotEquals(LocalDate.now(), paidPayment.getPaymentDate());
    }

    @Test
    void closeLoan_LoanNotFound_ThrowsLoanNotFoundException() {
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.empty());
        String expectedMessage = "Préstamo con ID " + LOAN_ID + " no encontrado.";

        LoanNotFoundException exception = assertThrows(LoanNotFoundException.class, () ->
                loanStatusService.closeLoan(authenticatedUser, LOAN_ID)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(loanRepository, times(1)).findById(LOAN_ID);
        verify(loanRepository, never()).save(any());
    }

    @Test
    void closeLoan_DifferentUser_ThrowsValidationException() {
        Client differentClient = new Client();
        differentClient.setUser(differentUser);
        loan.setClient(differentClient);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        String expectedMessage = "No tenés permiso para cerrar este préstamo.";

        ValidationException exception = assertThrows(ValidationException.class, () ->
                loanStatusService.closeLoan(authenticatedUser, LOAN_ID)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(loanRepository, times(1)).findById(LOAN_ID);
        verify(loanRepository, never()).save(any());
    }

    @Test
    void closeLoan_ClientUserNull_ThrowsValidationException() {
        client.setUser(null);
        loan.setClient(client);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));

        assertThrows(NullPointerException.class, () ->
                loanStatusService.closeLoan(authenticatedUser, LOAN_ID)
        );

        verify(loanRepository, times(1)).findById(LOAN_ID);
        verify(loanRepository, never()).save(any());
    }

    @Test
    void closeLoan_ClientNull_ThrowsValidationException() {
        loan.setClient(null);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));

        assertThrows(NullPointerException.class, () ->
                loanStatusService.closeLoan(authenticatedUser, LOAN_ID)
        );

        verify(loanRepository, times(1)).findById(LOAN_ID);
        verify(loanRepository, never()).save(any());
    }

    @Test
    void closeLoan_AllPaymentsAlreadyPaid_Success() {
        paidPayment.setPaid(true);
        paidPayment.setPaymentDate(LocalDate.now().minusDays(5));

        Payment anotherPaidPayment = new Payment();
        anotherPaidPayment.setPaid(true);
        anotherPaidPayment.setPaymentDate(LocalDate.now().minusDays(3));

        loan.setCuotas(Arrays.asList(paidPayment, anotherPaidPayment));

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        assertDoesNotThrow(() -> loanStatusService.closeLoan(authenticatedUser, LOAN_ID));

        // Assert
        verify(loanRepository, times(1)).findById(LOAN_ID);
        verify(loanRepository, times(1)).save(loan);
        assertEquals(LoanStatus.CERRADO, loan.getLoanStatus());

        assertEquals(LocalDate.now().minusDays(5), paidPayment.getPaymentDate());
        assertEquals(LocalDate.now().minusDays(3), anotherPaidPayment.getPaymentDate());
    }

    @Test
    void closeLoan_NoPayments_Success() {
        loan.setCuotas(Arrays.asList());

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        assertDoesNotThrow(() -> loanStatusService.closeLoan(authenticatedUser, LOAN_ID));
        verify(loanRepository, times(1)).findById(LOAN_ID);
        verify(loanRepository, times(1)).save(loan);
        assertEquals(LoanStatus.CERRADO, loan.getLoanStatus());
    }

    @Test
    void closeLoan_NullPaymentsList_Success() {
        loan.setCuotas(Collections.emptyList());
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        assertDoesNotThrow(() -> loanStatusService.closeLoan(authenticatedUser, LOAN_ID));

        verify(loanRepository, times(1)).findById(LOAN_ID);
        verify(loanRepository, times(1)).save(loan);
        assertEquals(LoanStatus.CERRADO, loan.getLoanStatus());
    }

    @Test
    void closeLoan_MultipleUnpaidPayments_MarksAllAsPaid() {
        Payment unpaid1 = new Payment();
        unpaid1.setPaid(false);
        unpaid1.setPaymentDate(null);

        Payment unpaid2 = new Payment();
        unpaid2.setPaid(false);
        unpaid2.setPaymentDate(null);

        Payment unpaid3 = new Payment();
        unpaid3.setPaid(false);
        unpaid3.setPaymentDate(null);

        loan.setCuotas(Arrays.asList(unpaid1, unpaid2, unpaid3));

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        assertDoesNotThrow(() -> loanStatusService.closeLoan(authenticatedUser, LOAN_ID));

        verify(loanRepository, times(1)).findById(LOAN_ID);
        verify(loanRepository, times(1)).save(loan);
        assertEquals(LoanStatus.CERRADO, loan.getLoanStatus());

        assertTrue(unpaid1.getPaid());
        assertTrue(unpaid2.getPaid());
        assertTrue(unpaid3.getPaid());

        assertNotNull(unpaid1.getPaymentDate());
        assertNotNull(unpaid2.getPaymentDate());
        assertNotNull(unpaid3.getPaymentDate());

        assertEquals(LocalDate.now(), unpaid1.getPaymentDate());
        assertEquals(LocalDate.now(), unpaid2.getPaymentDate());
        assertEquals(LocalDate.now(), unpaid3.getPaymentDate());
    }

    @Test
    void closeLoan_AuthenticatedUserNull_ThrowsException() {
        assertThrows(ValidationException.class, () ->
                loanStatusService.closeLoan(null, LOAN_ID)
        );

        verify(loanRepository, never()).findById(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void closeLoan_LoanIdNull_ThrowsException() {
        assertThrows(LoanNotFoundException.class, () ->
                loanStatusService.closeLoan(authenticatedUser, null)
        );

        verify(loanRepository, never()).findById(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void closeLoan_RepositorySaveThrowsException_PropagatesException() {
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        RuntimeException expectedException = new RuntimeException("Error de base de datos");
        when(loanRepository.save(any(Loan.class))).thenThrow(expectedException);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                loanStatusService.closeLoan(authenticatedUser, LOAN_ID)
        );

        assertEquals(expectedException, exception);
        verify(loanRepository, times(1)).findById(LOAN_ID);
        verify(loanRepository, times(1)).save(loan);
    }
}