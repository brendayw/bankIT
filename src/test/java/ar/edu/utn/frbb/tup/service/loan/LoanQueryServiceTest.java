package ar.edu.utn.frbb.tup.service.loan;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanDetailsDto;
import ar.edu.utn.frbb.tup.model.loan.dto.LoansListDto;
import ar.edu.utn.frbb.tup.model.loan.exceptions.LoanNotFoundException;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanQueryServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private LoanQueryService loanQueryService;

    private User authenticatedUser;
    private User differentUser;
    private Client client;
    private Loan loan;
    private Person person;
    private final Long LOAN_ID = 1L;
    private final Long CLIENT_DNI = 12345678L;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        differentUser = new User();
        differentUser.setId(2L);
        person = new Person();
        person.setDni(CLIENT_DNI);
        client = new Client();
        client.setUser(authenticatedUser);
        client.setPerson(person);
        loan = new Loan();
        loan.setId(LOAN_ID);
        loan.setClient(client);
        pageable = PageRequest.of(0, 10, Sort.by("id"));
    }

    @Test
    void findLoanById_Success() {
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));

        LoanDetailsDto result = loanQueryService.findLoanById(authenticatedUser, LOAN_ID);

        assertNotNull(result);
        verify(loanRepository, times(1)).findById(LOAN_ID);
    }

    @Test
    void findLoanById_LoanNotFound_ThrowsLoanNotFoundException() {
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.empty());
        String expectedMessage = "Préstamo no encontrado";

        LoanNotFoundException exception = assertThrows(LoanNotFoundException.class, () ->
                loanQueryService.findLoanById(authenticatedUser, LOAN_ID)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(loanRepository, times(1)).findById(LOAN_ID);
    }

    @Test
    void findLoanById_DifferentUser_ThrowsValidationException() {
        Client differentClient = new Client();
        differentClient.setUser(differentUser);
        loan.setClient(differentClient);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        String expectedMessage = "No tenés permiso para este préstamo";

        ValidationException exception = assertThrows(ValidationException.class, () ->
                loanQueryService.findLoanById(authenticatedUser, LOAN_ID)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(loanRepository, times(1)).findById(LOAN_ID);
    }

    @Test
    void findLoanById_ClientUserNull_ThrowsValidationException() {
        client.setUser(null);
        loan.setClient(client);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        String expectedMessage = "No tenés permiso para este préstamo";

        ValidationException exception = assertThrows(ValidationException.class, () ->
                loanQueryService.findLoanById(authenticatedUser, LOAN_ID)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(loanRepository, times(1)).findById(LOAN_ID);
    }

    @Test
    void findLoanById_ClientNull_ThrowsValidationException() {
        loan.setClient(null);
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        String expectedMessage = "No tenés permiso para este préstamo";

        ValidationException exception = assertThrows(ValidationException.class, () ->
                loanQueryService.findLoanById(authenticatedUser, LOAN_ID)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(loanRepository, times(1)).findById(LOAN_ID);
    }


    @Test
    void findLoansByClient_Success() {
        Page<Loan> loanPage = new PageImpl<>(Collections.singletonList(loan));

        when(clientRepository.findByPersonDni(CLIENT_DNI)).thenReturn(Optional.of(client));
        when(loanRepository.findByClientPersonDni(eq(CLIENT_DNI), any(Pageable.class))).thenReturn(loanPage);

        Page<LoansListDto> result = loanQueryService.findLoansByClient(authenticatedUser, CLIENT_DNI, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(clientRepository, times(1)).findByPersonDni(CLIENT_DNI);
        verify(loanRepository, times(1)).findByClientPersonDni(CLIENT_DNI, pageable);
    }

    @Test
    void findLoansByClient_EmptyResult_Success() {
        Page<Loan> emptyPage = new PageImpl<>(Collections.emptyList());

        when(clientRepository.findByPersonDni(CLIENT_DNI)).thenReturn(Optional.of(client));
        when(loanRepository.findByClientPersonDni(eq(CLIENT_DNI), any(Pageable.class))).thenReturn(emptyPage);

        Page<LoansListDto> result = loanQueryService.findLoansByClient(authenticatedUser, CLIENT_DNI, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(clientRepository, times(1)).findByPersonDni(CLIENT_DNI);
        verify(loanRepository, times(1)).findByClientPersonDni(CLIENT_DNI, pageable);
    }

    @Test
    void findLoansByClient_ClientNotFound_ThrowsClientNotFoundException() {
        when(clientRepository.findByPersonDni(CLIENT_DNI)).thenReturn(Optional.empty());
        String expectedMessage = "Cliente no encontrado";

        ClientNotFoundException exception = assertThrows(ClientNotFoundException.class, () ->
                loanQueryService.findLoansByClient(authenticatedUser, CLIENT_DNI, pageable)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(clientRepository, times(1)).findByPersonDni(CLIENT_DNI);
        verify(loanRepository, never()).findByClientPersonDni(any(), any());
    }

    @Test
    void findLoansByClient_DifferentUser_ThrowsValidationException() {
        Client differentClient = new Client();
        differentClient.setUser(differentUser);

        when(clientRepository.findByPersonDni(CLIENT_DNI)).thenReturn(Optional.of(differentClient));
        String expectedMessage = "No tenés permiso para estos préstamos";

        ValidationException exception = assertThrows(ValidationException.class, () ->
                loanQueryService.findLoansByClient(authenticatedUser, CLIENT_DNI, pageable)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(clientRepository, times(1)).findByPersonDni(CLIENT_DNI);
        verify(loanRepository, never()).findByClientPersonDni(any(), any());
    }

    @Test
    void findLoansByClient_ClientUserNull_ThrowsValidationException() {
        client.setUser(null);

        when(clientRepository.findByPersonDni(CLIENT_DNI)).thenReturn(Optional.of(client));
        String expectedMessage = "No tenés permiso para estos préstamos";

        ValidationException exception = assertThrows(ValidationException.class, () ->
                loanQueryService.findLoansByClient(authenticatedUser, CLIENT_DNI, pageable)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(clientRepository, times(1)).findByPersonDni(CLIENT_DNI);
        verify(loanRepository, never()).findByClientPersonDni(any(), any());
    }

    @Test
    void findLoansByClient_MultipleLoans_Success() {
        Loan loan2 = new Loan();
        loan2.setId(2L);
        loan2.setClient(client);

        Page<Loan> loanPage = new PageImpl<>(java.util.Arrays.asList(loan, loan2));

        when(clientRepository.findByPersonDni(CLIENT_DNI)).thenReturn(Optional.of(client));
        when(loanRepository.findByClientPersonDni(eq(CLIENT_DNI), any(Pageable.class))).thenReturn(loanPage);

        Page<LoansListDto> result = loanQueryService.findLoansByClient(authenticatedUser, CLIENT_DNI, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(clientRepository, times(1)).findByPersonDni(CLIENT_DNI);
        verify(loanRepository, times(1)).findByClientPersonDni(CLIENT_DNI, pageable);
    }

    @Test
    void findLoansByClient_WithPagination_Success() {
        Pageable customPageable = PageRequest.of(1, 5, Sort.by("createDate").descending());
        Page<Loan> loanPage = new PageImpl<>(Collections.singletonList(loan));

        when(clientRepository.findByPersonDni(CLIENT_DNI)).thenReturn(Optional.of(client));
        when(loanRepository.findByClientPersonDni(eq(CLIENT_DNI), eq(customPageable))).thenReturn(loanPage);

        Page<LoansListDto> result = loanQueryService.findLoansByClient(authenticatedUser, CLIENT_DNI, customPageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(clientRepository, times(1)).findByPersonDni(CLIENT_DNI);
        verify(loanRepository, times(1)).findByClientPersonDni(CLIENT_DNI, customPageable);
    }
}