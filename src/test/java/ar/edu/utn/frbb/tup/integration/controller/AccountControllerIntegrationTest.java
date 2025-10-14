package ar.edu.utn.frbb.tup.integration.controller;

import ar.edu.utn.frbb.tup.Application;
import ar.edu.utn.frbb.tup.config.IntegrationTestBase;
import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.service.account.AccountCreationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Transactional
public class AccountControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountCreationService accountCreationService;

    private AccountDto accountDto;

    @BeforeEach
    public void setUp() {
        accountDto = new AccountDto(
                12345678L,
                CurrencyType.PESOS,
                AccountType.CAJA_AHORRO,
                1000.0
        );
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldCreateAccountForClient() throws Exception {
        Client mockClient = new Client();
        Person mockPerson = Person.builder()
                .dni(12345678L)
                .nombre("User")
                .apellido("Test")
                .build();

        mockClient.setPerson(mockPerson);

        Account mockAccount = Account.builder()
                .id(1L)
                .client(mockClient)
                .creationDate(LocalDate.now())
                .balance(1000.0)
                .accountType(AccountType.CAJA_AHORRO)
                .currencyType(CurrencyType.PESOS)
                .active(true)
                .build();

        when(accountCreationService.createAccount(any(), any(AccountDto.class)))
                .thenReturn(mockAccount);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.balance").value(1000.0))
                .andExpect(jsonPath("$.accountType").value("CAJA_AHORRO"))
                .andExpect(jsonPath("$.currencyType").value("PESOS"));
    }
}