package ar.edu.utn.frbb.tup.unit.service.account;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.exceptions.AccountNotFoundException;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.AccountRepository;
import ar.edu.utn.frbb.tup.service.account.AccountManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountManagementServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountManagementService accountManagementService;

    @Test
    void deactivateAccount_WithAuthorizedUser_DeactivatesAccount() {
        Long accountId = 1L;
        User user = User.builder().id(1L).build();
        Client client = Client.builder().id(1L).build();
        user.associateWithClient(client);

        Account account = Account.builder().id(accountId).client(client).active(true).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        accountManagementService.deactivateAccount(user, accountId);

        assertThat(account.isActive()).isFalse();
        verify(accountRepository).save(account);
    }

    @Test
    void deactivateAccount_WithUnauthorizedUser_ThrowsException() {
        Long accountId = 1L;
        User authorizedUser = User.builder().id(1L).build();
        User differentUser = User.builder().id(2L).build();

        Client authorizedUserClient = Client.builder().id(1L).build();
        authorizedUser.associateWithClient(authorizedUserClient);

        Client differentUserClient = Client.builder().id(2L).build();
        differentUser.associateWithClient(differentUserClient);

        Account account = Account.builder().id(accountId).client(differentUserClient).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(ValidationException.class, () -> {
            accountManagementService.deactivateAccount(authorizedUser, accountId);
        });
    }

    @Test
    void deactivateAccount_WithNonExistentAccount_ThrowsException() {
        Long accountId = 999L;
        User user = User.builder().id(1L).build();
        Client client = Client.builder().id(1L).build();
        user.associateWithClient(client);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            accountManagementService.deactivateAccount(user, accountId);
        });
    }

    @Test
    void deactivateAccount_WithUserWithoutClient_ThrowsException() {
        Long accountId = 1L;
        User user = User.builder().id(1L).build();

        assertThrows(IllegalStateException.class, () -> {
            accountManagementService.deactivateAccount(user, accountId);
        });
    }
}