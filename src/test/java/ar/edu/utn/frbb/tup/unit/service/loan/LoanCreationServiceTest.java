package ar.edu.utn.frbb.tup.unit.service.loan;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import ar.edu.utn.frbb.tup.service.loan.LoanCreationService;
import ar.edu.utn.frbb.tup.service.loan.LoanSimulatorService;
import ar.edu.utn.frbb.tup.service.payment.PaymentPlanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanCreationServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private LoanSimulatorService simulatorService;

    @Mock
    private PaymentPlanService paymentPlanService;

    @InjectMocks
    private LoanCreationService loanCreationService;

    @Test
    void registerLoan_WithValidData_ReturnsLoan() {
        User user = User.builder().id(1L).username("test").build();
        Client client = Client.builder().id(1L).user(user).build();
        LoanRequestDto dto = new LoanRequestDto(
                12345678L,
                50000.0,
                CurrencyType.PESOS,
                AccountType.CAJA_AHORRO,
                12);
        Loan simulatedLoan = Loan.builder().id(1L).loanStatus(LoanStatus.APROBADO).build();

        when(clientRepository.findByPersonDni(12345678L)).thenReturn(Optional.of(client));
        when(simulatorService.createLoan(client, dto)).thenReturn(simulatedLoan);
        when(loanRepository.save(any(Loan.class))).thenReturn(simulatedLoan);

        Loan result = loanCreationService.registerLoan(user, dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(clientRepository).findByPersonDni(12345678L);
        verify(loanRepository).save(simulatedLoan);
    }

    @Test
    void registerLoan_WithNonExistentClient_ThrowsException() {
        User user = User.builder().id(1L).build();
        LoanRequestDto dto = new LoanRequestDto(99999999L, 50000.0, CurrencyType.PESOS, AccountType.CAJA_AHORRO, 12);

        when(clientRepository.findByPersonDni(99999999L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> {
            loanCreationService.registerLoan(user, dto);
        });
    }

    @Test
    void registerLoan_WithUnauthorizedUser_ThrowsException() {
        User authenticatedUser = User.builder().id(1L).build();
        User differentUser = User.builder().id(2L).build();
        Client client = Client.builder().id(1L).user(differentUser).build();
        LoanRequestDto dto = new LoanRequestDto(12345678L, 50000.0, CurrencyType.PESOS, AccountType.CAJA_AHORRO, 12);
        when(clientRepository.findByPersonDni(12345678L)).thenReturn(Optional.of(client));
        assertThrows(ValidationException.class, () -> {
            loanCreationService.registerLoan(authenticatedUser, dto);
        });
    }
}
