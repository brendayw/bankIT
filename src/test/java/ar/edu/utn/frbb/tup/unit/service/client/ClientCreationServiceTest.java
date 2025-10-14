package ar.edu.utn.frbb.tup.unit.service.client;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDto;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.person.dto.PersonDto;
import ar.edu.utn.frbb.tup.model.person.enums.PersonType;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.service.client.ClientCreationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientCreationServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientCreationService clientCreationService;

    private ClientDto createValidClientDto() {
        PersonDto personDto = new PersonDto(12345678L, "Pérez", "Juan",
                LocalDate.of(1990, 1, 1), "123456789", "juan@email.com");
        return new ClientDto(personDto, PersonType.PERSONA_FISICA);
    }

    private User createUserWithoutClient() {
        return User.builder()
                .id(1L)
                .username("juanperez")
                .password("encodedPassword")
                .build();
    }

    private User createUserWithClient() {
        User user = User.builder()
                .id(2L)
                .username("mariagarcia")
                .password("encodedPassword")
                .build();
        Client existingClient = Client.builder().id(1L).build();
        user.associateWithClient(existingClient);
        return user;
    }

    @Test
    void createClient_WithValidData_ReturnsClient() {
        ClientDto dto = createValidClientDto();
        User user = createUserWithoutClient();
        Client expectedClient = Client.builder().id(1L).build();

        when(clientRepository.existsByPersonDni(12345678L)).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(expectedClient);

        Client result = clientCreationService.createClient(dto, user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(clientRepository).existsByPersonDni(12345678L);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void createClient_WithExistingDni_ThrowsClientAlreadyExistsException() {
        // Given
        ClientDto dto = createValidClientDto();
        User user = createUserWithoutClient();

        when(clientRepository.existsByPersonDni(12345678L)).thenReturn(true);

        assertThrows(ClientAlreadyExistsException.class, () -> {
            clientCreationService.createClient(dto, user);
        });

        verify(clientRepository).existsByPersonDni(12345678L);
    }

    @Test
    void createClient_WithUserAlreadyHavingClient_ThrowsValidationException() {
        ClientDto dto = createValidClientDto();
        User user = createUserWithClient();

        when(clientRepository.existsByPersonDni(12345678L)).thenReturn(false);

        assertThrows(ValidationException.class, () -> {
            clientCreationService.createClient(dto, user);
        });

        verify(clientRepository).existsByPersonDni(12345678L);
    }

    @Test
    void createClient_WithDifferentDni_CreatesSuccessfully() {
        PersonDto personDto = new PersonDto(
                87654321L,
                "García",
                "María",
                LocalDate.of(1985, 5, 15),
                "987654321",
                "maria@email.com"
        );
        ClientDto dto = new ClientDto(personDto, PersonType.PERSONA_FISICA);
        User user = createUserWithoutClient();
        Client expectedClient = Client.builder().id(2L).build();

        when(clientRepository.existsByPersonDni(87654321L)).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(expectedClient);

        Client result = clientCreationService.createClient(dto, user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        verify(clientRepository).existsByPersonDni(87654321L);
    }

    @Test
    void createClient_WithJuridicaPersonType_CreatesSuccessfully() {
        PersonDto personDto = new PersonDto(
                20345678901L,
                "Empresa SA",
                "Empresa",
                LocalDate.of(2000, 1, 1),
                "111222333",
                "empresa@email.com"
        );
        ClientDto dto = new ClientDto(personDto, PersonType.PERSONA_JURIDICA);
        User user = createUserWithoutClient();
        Client expectedClient = Client.builder().id(3L).build();

        when(clientRepository.existsByPersonDni(20345678901L)).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(expectedClient);

        Client result = clientCreationService.createClient(dto, user);

        assertThat(result).isNotNull();
        verify(clientRepository).existsByPersonDni(20345678901L);
    }

    @Test
    void createClient_VerifiesUserClientAssociation() {
        ClientDto dto = createValidClientDto();
        User user = createUserWithoutClient();
        Client savedClient = Client.builder().id(1L).build();

        when(clientRepository.existsByPersonDni(12345678L)).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client clientToSave = invocation.getArgument(0);

            assertThat(clientToSave.getUser()).isEqualTo(user);
            assertThat(user.getClient()).isEqualTo(clientToSave);
            return savedClient;
        });

        Client result = clientCreationService.createClient(dto, user);

        assertThat(result).isEqualTo(savedClient);
        assertThat(user.getClient()).isNotNull();
    }

    @Test
    void createClient_WithMinimumValidData_CreatesSuccessfully() {
        PersonDto minimalPersonDto = new PersonDto(
                11111111L,
                "Apellido",
                "Nombre",
                LocalDate.now(),
                "123",
                "a@b.com"
        );
        ClientDto minimalDto = new ClientDto(minimalPersonDto, PersonType.PERSONA_FISICA);
        User user = createUserWithoutClient();
        Client expectedClient = Client.builder().id(1L).build();

        when(clientRepository.existsByPersonDni(11111111L)).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(expectedClient);

        Client result = clientCreationService.createClient(minimalDto, user);

        assertThat(result).isNotNull();
        verify(clientRepository).existsByPersonDni(11111111L);
    }
}