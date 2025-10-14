package ar.edu.utn.frbb.tup.unit.service.account;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDetailsDto;
import ar.edu.utn.frbb.tup.model.account.dto.AccountsListDto;
import ar.edu.utn.frbb.tup.model.account.exceptions.AccountNotFoundException;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.AccountRepository;
import ar.edu.utn.frbb.tup.service.account.AccountQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountQueryServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountQueryService accountQueryService;

    @Test
    void findAccountById_WithAuthorizedUser_ReturnsAccount() {
        Long accountId = 1L;
        User authenticatedUser = User.builder().id(1L).build();
        Client client = Client.builder().id(1L).user(authenticatedUser).build();
        authenticatedUser.associateWithClient(client);
        Account account = Account.builder().id(accountId).client(client).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountDetailsDto result = accountQueryService.findAccountById(accountId, authenticatedUser);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(accountId);
    }

    @Test
    void findAccountById_WithUnauthorizedUser_ThrowsException() {
        Long accountId = 1L;
        User authenticatedUser = User.builder().id(1L).build();
        User differentUser = User.builder().id(2L).build();
        Client client = Client.builder().id(1L).user(differentUser).build();
        Account account = Account.builder().id(accountId).client(client).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(ValidationException.class, () -> {
            accountQueryService.findAccountById(accountId, authenticatedUser);
        });
    }

    @Test
    void findAccountById_WithNonExistentAccount_ThrowsException() {
        Long accountId = 999L;
        User user = User.builder().id(1L).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            accountQueryService.findAccountById(accountId, user);
        });
    }

    @Test
    void findAccountById_WithUserWithoutClient_ThrowsException() {
        Long accountId = 1L;
        User user = User.builder().id(1L).build();
        Account account = Account.builder().id(accountId).client(Client.builder().id(1L).build()).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(ValidationException.class, () -> {
            accountQueryService.findAccountById(accountId, user);
        });
    }

    @Test
    void findAllAccountsByAuthenticatedUser_WithValidUser_ReturnsAccounts() {
        User user = User.builder().id(1L).build();
        Client client = Client.builder().id(1L).user(user).person(Person.builder().dni(12345678L).build()).build();
        user.associateWithClient(client);

        Account account1 = Account.builder().id(1L).balance(1000.0).active(true).build();
        Account account2 = Account.builder().id(2L).balance(2000.0).active(true).build();
        List<Account> accounts = List.of(account1, account2);

        when(accountRepository.findAccountsByClientPersonDniAndActiveTrue(any(), any()))
                .thenReturn(new PageImpl<>(accounts));

        AccountsListDto result = accountQueryService.findAllAccountsByAuthenticatedUser(user, PageRequest.of(0, 10));

        assertThat(result.accounts()).hasSize(2);
    }

    @Test
    void findAllAccountsByAuthenticatedUser_WithUserWithoutClient_ThrowsException() {
        User user = User.builder().id(1L).build();

        assertThrows(IllegalStateException.class, () -> {
            accountQueryService.findAllAccountsByAuthenticatedUser(user, PageRequest.of(0, 10));
        });
    }
}
