package ar.edu.utn.frbb.tup.integration.controller;

import ar.edu.utn.frbb.tup.Application;
import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDetailsDto;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDto;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.model.person.dto.PersonDto;
import ar.edu.utn.frbb.tup.model.person.enums.PersonType;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.service.client.ClientCreationService;
import ar.edu.utn.frbb.tup.service.client.ClientQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Transactional
public class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientCreationService clientCreationService;

    @MockBean
    private ClientQueryService clientQueryService;

    private PersonDto personDto;
    private ClientDto clientDto;

    @BeforeEach
    void setUp() {
        personDto = new PersonDto(
                12345678L,
                "Test",
                "User",
                LocalDate.of(2001,06,07),
                "2915896347",
                "user@test.com"
        );
        clientDto = new ClientDto(
                personDto,
                PersonType.PERSONA_FISICA
        );
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldCreateClientSuccessfully() throws Exception {
        Client mockClient = new Client();
        Person mockPerson = Person.builder()
                .dni(12345678L)
                .nombre("User")
                .apellido("Test")
                .fechaNacimiento(LocalDate.of(2001, 6, 7))
                .telefono("2915896347")
                .email("user@test.com")
                .build();

        mockClient.setPerson(mockPerson);
        mockClient.setPersonType(PersonType.PERSONA_FISICA);

        User customUser = new User();
        customUser.setId(1L);
        customUser.setUsername("testuser");
        customUser.setClient(mockClient);

        when(clientCreationService.createClient(any(ClientDto.class), any(User.class)))
                .thenReturn(mockClient);

        mockMvc.perform(post("/api/clients")
                        .with(request -> {
                            Authentication auth = new UsernamePasswordAuthenticationToken(
                                    customUser, null,
                                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
                            );
                            request.setUserPrincipal(auth);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dni").value(12345678L))
                .andExpect(jsonPath("$.apellido").value("Test"))
                .andExpect(jsonPath("$.nombre").value("User"))
                .andExpect(jsonPath("$.telefono").value("2915896347"))
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.personType").value("PERSONA_FISICA"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldGetClientDetailsWithAccountsAndLoans() throws Exception {
        Client mockClient = new Client();
        mockClient.setId(1L);

        Person mockPerson = Person.builder()
                .dni(12345678L)
                .nombre("User")
                .apellido("Test")
                .build();
        mockClient.setPerson(mockPerson);

        mockClient.setAccounts(new HashSet<>());

        Account account = Account.builder()
                .id(1L)
                .balance(1000.0)
                .accountType(AccountType.CAJA_AHORRO)
                .client(mockClient)
                .build();
        mockClient.addAccount(account);

        User customUser = new User();
        customUser.setId(1L);
        customUser.setUsername("testuser");
        customUser.setClient(mockClient);

        ClientDetailsDto dto = new ClientDetailsDto(mockClient);

        when(clientQueryService.getOwnClientProfile(customUser)).thenReturn(dto);

        mockMvc.perform(get("/api/clients/me")
                .with(SecurityMockMvcRequestPostProcessors.user(customUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.dni").value(12345678L))
                .andExpect(jsonPath("$.cuentas").isArray())
                .andExpect(jsonPath("$.cuentas[0].id").value(1L))
                .andExpect(jsonPath("$.cuentas[0].balance").value(1000.0));
    }
}
