package ar.edu.utn.frbb.tup.integration.controller;

import ar.edu.utn.frbb.tup.Application;
import ar.edu.utn.frbb.tup.config.IntegrationTestBase;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.service.loan.LoanCreationService;
import ar.edu.utn.frbb.tup.service.loan.LoanPaymentService;
import ar.edu.utn.frbb.tup.service.loan.LoanQueryService;
import ar.edu.utn.frbb.tup.service.loan.LoanStatusService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Transactional
public class LoanControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock de los servicios que el controlador necesita
    @MockBean
    private LoanCreationService loanCreationService;

    @MockBean
    private LoanQueryService loanQueryService;

    @MockBean
    private LoanPaymentService loanPaymentService;

    @MockBean
    private LoanStatusService loanStatusService;

    private LoanRequestDto loanRequest;

    @BeforeEach
    public void setUp() {
        loanRequest = new LoanRequestDto(
                12345678L, 100.0, CurrencyType.PESOS,
                AccountType.CUENTA_CORRIENTE, 12);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void shouldRegisterLoanSuccessfully() throws Exception {
        Client mockClient = new Client();
        Person mockPerson = Person.builder()
                .dni(12345678L)
                .nombre("User")
                .apellido("Test")
                .build();

        mockClient.setPerson(mockPerson);

        Loan mockLoan = Loan.builder()
                .id(1L)
                .client(mockClient)
                .requestedAmount(100.0)
                .currencyType(CurrencyType.PESOS)
                .accountType(AccountType.CUENTA_CORRIENTE)
                .termInMonths(12)
                .build();

        when(loanCreationService.registerLoan(any(), any(LoanRequestDto.class)))
                .thenReturn(mockLoan);

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.dni").value(12345678L))
                .andExpect(jsonPath("$.currencyType").value("PESOS"))
                .andExpect(jsonPath("$.termInMonths").value(12));
    }

    @Test
    void shouldPayLoanInstallmentSuccessfully() {

    }
}