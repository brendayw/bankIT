package ar.edu.utn.frbb.tup.unit.service.account;

import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.account.exceptions.AccountAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.repository.AccountRepository;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.service.account.AccountCreationService;
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
public class AccountCreationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private AccountCreationService accountCreationService;

    @Test
    void createAccount_WithValidData_ReturnsAccount() {
        // Given
        Long clientDni = 12345678L;
        Client client = Client.builder().id(1L).build();
        AccountDto dto = new AccountDto(clientDni, CurrencyType.PESOS, AccountType.CAJA_AHORRO, 1200000.0);
        Account expectedAccount = Account.builder().id(1L).balance(1200000.0).build();

        when(clientRepository.findByPersonDni(clientDni)).thenReturn(Optional.of(client));
        when(accountRepository.existsByClientPersonDniAndAccountTypeAndCurrencyType(
                clientDni, AccountType.CAJA_AHORRO, CurrencyType.PESOS)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(expectedAccount);

        // When
        Account result = accountCreationService.createAccount(clientDni, dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBalance()).isEqualTo(1200000.0);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_WithNullBalance_SetsZeroBalance() {
        Long clientDni = 12345678L;
        Client client = Client.builder().id(1L).build();
        AccountDto dto = new AccountDto(clientDni, CurrencyType.PESOS, AccountType.CAJA_AHORRO, null);

        when(clientRepository.findByPersonDni(clientDni)).thenReturn(Optional.of(client));
        when(accountRepository.existsByClientPersonDniAndAccountTypeAndCurrencyType(any(), any(), any())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountCreationService.createAccount(clientDni, dto);

        assertThat(result.getBalance()).isEqualTo(0.0);
    }

    @Test
    void createAccount_WithExistingAccountType_ThrowsException() {
        Long clientDni = 12345678L;
        Client client = Client.builder().id(1L).build();
        AccountDto dto = new AccountDto(clientDni, CurrencyType.PESOS, AccountType.CAJA_AHORRO, 1000.0);

        when(clientRepository.findByPersonDni(clientDni)).thenReturn(Optional.of(client));
        when(accountRepository.existsByClientPersonDniAndAccountTypeAndCurrencyType(
                clientDni, AccountType.CAJA_AHORRO, CurrencyType.PESOS)).thenReturn(true);

        // When & Then
        assertThrows(AccountAlreadyExistsException.class, () -> {
            accountCreationService.createAccount(clientDni, dto);
        });
    }

    @Test
    void createAccount_WithNonExistentClient_ThrowsException() {
        Long clientDni = 99999999L;
        AccountDto dto = new AccountDto(clientDni, CurrencyType.PESOS, AccountType.CAJA_AHORRO, 1000.0);

        when(clientRepository.findByPersonDni(clientDni)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> {
            accountCreationService.createAccount(clientDni, dto);
        });
    }
}