package ar.edu.utn.frbb.tup.unit.controller;

import ar.edu.utn.frbb.tup.controller.AccountController;
import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.dto.*;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.service.account.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AccountControllerTest {
    @Mock
    private AccountCreationService creationService;

    @Mock
    private AccountQueryService queryService;

    @Mock
    private AccountManagementService managementService;

    @InjectMocks
    private AccountController accountController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("userTest");
    }

    @Test
    void shouldRegisterAccountSuccessfully() {
        AccountDto dto = new AccountDto(12345678L, CurrencyType.PESOS, AccountType.CAJA_AHORRO, 1200000.00 );
        Account accountMock = Account.builder()
                .id(1L)
                .balance(1200000.00)
                .accountType(AccountType.CAJA_AHORRO)
                .currencyType(CurrencyType.PESOS)
                .build();

        when(creationService.createAccount(dto.dni(), dto)).thenReturn(accountMock);

        ResponseEntity<?> response = accountController.register(dto, UriComponentsBuilder.newInstance());

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isInstanceOf(AccountDetailsDto.class);
        verify(creationService).createAccount(dto.dni(), dto);
    }

    @Test
    void shouldGetAccountById() {
        Long accountId = 1L;
        AccountDetailsDto detailsDto = new AccountDetailsDto(accountId, 500.00, AccountType.CAJA_AHORRO, CurrencyType.PESOS);
        when(queryService.findAccountById(accountId, user)).thenReturn(detailsDto);

        ResponseEntity<?> response = accountController.getAccountById(accountId, user);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(detailsDto);
        verify(queryService).findAccountById(accountId, user);
    }

    @Test
    void shouldGetAccountsByClient() {
        var pagination = PageRequest.of(0, 10);
        var accountsListDto = new AccountsListDto(List.of());
        when(queryService.findAllAccountsByAuthenticatedUser(user, pagination)).thenReturn(accountsListDto);

        ResponseEntity<?> response = accountController.getAccountsByClient(user, pagination);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(accountsListDto);
        verify(queryService).findAllAccountsByAuthenticatedUser(user, pagination);
    }

    @Test
    void shouldDeactivateAccount() {
        Long accountId = 1L;

        ResponseEntity<?> response = accountController.delete(user, accountId);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(managementService).deactivateAccount(user, accountId);
    }
}
