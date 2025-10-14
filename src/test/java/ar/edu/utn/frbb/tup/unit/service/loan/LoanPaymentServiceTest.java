package ar.edu.utn.frbb.tup.unit.service.loan;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.exceptions.LoanNotFoundException;
import ar.edu.utn.frbb.tup.model.payment.dto.UpdatePaymentDto;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import ar.edu.utn.frbb.tup.service.loan.LoanPaymentService;
import ar.edu.utn.frbb.tup.service.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanPaymentServiceTest {

    @Mock
    private LoanRepository repository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private LoanPaymentService loanPaymentService;

    private User authenticatedUser;
    private User differentUser;
    private Client client;
    private Loan loan;
    private UpdatePaymentDto paymentDto;
    private final Long LOAN_ID = 11111111L;
    private final Long PAYMENT_ID = 22222222L;
    private final Long CLIENT_DNI = 1111111L;
    private final LocalDate PAYMENT_DATE = LocalDate.now();

    @BeforeEach
    void setUp() {
        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        differentUser = new User();
        differentUser.setId(2L);
        client = new Client();
        client.setUser(authenticatedUser);
        loan = new Loan();
        loan.setId(LOAN_ID);
        loan.setClient(client);
        paymentDto = new UpdatePaymentDto(PAYMENT_ID, CLIENT_DNI, PAYMENT_DATE);
    }

    @Test
    void payInstallment_Success() {
        when(repository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        doNothing().when(paymentService).markAsPaid(eq(authenticatedUser), eq(PAYMENT_ID));

        assertDoesNotThrow(() ->
                loanPaymentService.payInstallment(authenticatedUser, LOAN_ID, paymentDto)
        );

        verify(repository, times(1)).findById(LOAN_ID);
        verify(paymentService, times(1)).markAsPaid(authenticatedUser, PAYMENT_ID);
    }

    @Test
    void payInstallment_AuthenticatedUserNull_ThrowsIllegalArgumentException() {
        String expectedMessage = "El usuario autenticado no puede ser nulo";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                loanPaymentService.payInstallment(null, LOAN_ID, paymentDto)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(repository, never()).findById(any());
        verify(paymentService, never()).markAsPaid(any(), any());
    }

    @Test
    void payInstallment_LoanIdNull_ThrowsIllegalArgumentException() {
        String expectedMessage = "El ID del préstamo no puede ser nulo";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                loanPaymentService.payInstallment(authenticatedUser, null, paymentDto)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(repository, never()).findById(any());
        verify(paymentService, never()).markAsPaid(any(), any());
    }

    @Test
    void payInstallment_PaymentDtoNull_ThrowsIllegalArgumentException() {
        String expectedMessage = "El DTO de pago no puede ser nulo";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                loanPaymentService.payInstallment(authenticatedUser, LOAN_ID, null)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(repository, never()).findById(any());
        verify(paymentService, never()).markAsPaid(any(), any());
    }

    @Test
    void payInstallment_LoanNotFound_ThrowsLoanNotFoundException() {
        when(repository.findById(LOAN_ID)).thenReturn(Optional.empty());
        String expectedMessage = "Préstamo no encontrado";

        LoanNotFoundException exception = assertThrows(LoanNotFoundException.class, () ->
                loanPaymentService.payInstallment(authenticatedUser, LOAN_ID, paymentDto)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(repository, times(1)).findById(LOAN_ID);
        verify(paymentService, never()).markAsPaid(any(), any());
    }

    @Test
    void payInstallment_LoanClientNull_ThrowsValidationException() {
        loan.setClient(null);
        when(repository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        String expectedMessage = "No tenés permiso para pagar este préstamo";

        ValidationException exception = assertThrows(ValidationException.class, () ->
                loanPaymentService.payInstallment(authenticatedUser, LOAN_ID, paymentDto)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(repository, times(1)).findById(LOAN_ID);
        verify(paymentService, never()).markAsPaid(any(), any());
    }

    @Test
    void payInstallment_LoanClientUserNull_ThrowsValidationException() {
        client.setUser(null);
        loan.setClient(client);
        when(repository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        String expectedMessage = "No tenés permiso para pagar este préstamo";

        ValidationException exception = assertThrows(ValidationException.class, () ->
                loanPaymentService.payInstallment(authenticatedUser, LOAN_ID, paymentDto)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(repository, times(1)).findById(LOAN_ID);
        verify(paymentService, never()).markAsPaid(any(), any());
    }

    @Test
    void payInstallment_DifferentUser_ThrowsValidationException() {
        Client differentClient = new Client();
        differentClient.setUser(differentUser);
        loan.setClient(differentClient);

        when(repository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        String expectedMessage = "No tenés permiso para pagar este préstamo";

        ValidationException exception = assertThrows(ValidationException.class, () ->
                loanPaymentService.payInstallment(authenticatedUser, LOAN_ID, paymentDto)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(repository, times(1)).findById(LOAN_ID);
        verify(paymentService, never()).markAsPaid(any(), any());
    }

    @Test
    void payInstallment_PaymentServiceThrowsException_PropagatesException() {
        when(repository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        RuntimeException expectedException = new RuntimeException("Error en payment service");
        doThrow(expectedException).when(paymentService).markAsPaid(eq(authenticatedUser), eq(PAYMENT_ID));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                loanPaymentService.payInstallment(authenticatedUser, LOAN_ID, paymentDto)
        );

        assertEquals(expectedException, exception);
        verify(repository, times(1)).findById(LOAN_ID);
        verify(paymentService, times(1)).markAsPaid(authenticatedUser, PAYMENT_ID);
    }
}