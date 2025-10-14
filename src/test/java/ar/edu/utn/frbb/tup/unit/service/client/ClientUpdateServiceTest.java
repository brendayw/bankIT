package ar.edu.utn.frbb.tup.unit.service.client;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDetailsDto;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.service.client.ClientUpdateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientUpdateServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientUpdateService clientUpdateService;

    private User createUserWithClient(String telefono, String email) {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .build();

        Client client = Client.builder()
                .id(1L)
                .person(Person.builder()
                        .dni(12345678L)
                        .apellido("Pérez")
                        .nombre("Juan")
                        .fechaNacimiento(LocalDate.of(1990, 1, 1))
                        .telefono(telefono)
                        .email(email)
                        .build())
                .active(true)
                .build();

        user.associateWithClient(client);
        return user;
    }

    private User createUserWithoutClient() {
        return User.builder()
                .id(2L)
                .username("userwithoutclient")
                .password("encodedPassword")
                .build();
    }

    @Test
    void updateOwnClientDetailsWithPhoneAndEmailUpdatesSuccessfully() {
        User user = createUserWithClient("old-phone", "old@email.com");
        String nuevoTelefono = "123-456-789";
        String nuevoEmail = "nuevo@email.com";

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientDetailsDto result = clientUpdateService.updateOwnClientDetails(user, nuevoTelefono, nuevoEmail);

        assertThat(result).isNotNull();
        assertThat(result.telefono()).isEqualTo(nuevoTelefono);
        assertThat(result.email()).isEqualTo(nuevoEmail);
        verify(clientRepository).save(user.getClient());
    }

    @Test
    void updateOwnClientDetailsWithOnlyPhoneUpdatesPhoneOnly() {
        User user = createUserWithClient("old-phone", "old@email.com");
        String nuevoTelefono = "987-654-321";
        String emailNull = null;

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientDetailsDto result = clientUpdateService.updateOwnClientDetails(user, nuevoTelefono, emailNull);

        assertThat(result).isNotNull();
        assertThat(result.telefono()).isEqualTo(nuevoTelefono);
        assertThat(result.email()).isEqualTo("old@email.com");
        verify(clientRepository).save(user.getClient());
    }

    @Test
    void updateOwnClientDetailsWithOnlyEmailUpdatesEmailOnly() {
        User user = createUserWithClient("old-phone", "old@email.com");
        String telefonoNull = null;
        String nuevoEmail = "nuevo.email@test.com";

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientDetailsDto result = clientUpdateService.updateOwnClientDetails(user, telefonoNull, nuevoEmail);

        assertThat(result).isNotNull();
        assertThat(result.telefono()).isEqualTo("old-phone");
        assertThat(result.email()).isEqualTo(nuevoEmail);
        verify(clientRepository).save(user.getClient());
    }

    @Test
    void updateOwnClientDetailsWithBlankPhoneDoesNotUpdatePhone() {
        User user = createUserWithClient("old-phone", "old@email.com");
        String telefonoBlank = "   ";
        String nuevoEmail = "nuevo@email.com";

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientDetailsDto result = clientUpdateService.updateOwnClientDetails(user, telefonoBlank, nuevoEmail);

        assertThat(result).isNotNull();
        assertThat(result.telefono()).isEqualTo("old-phone");
        assertThat(result.email()).isEqualTo(nuevoEmail);
        verify(clientRepository).save(user.getClient());
    }

    @Test
    void updateOwnClientDetailsWithBlankEmailDoesNotUpdateEmail() {
        User user = createUserWithClient("old-phone", "old@email.com");
        String nuevoTelefono = "123-456-789";
        String emailBlank = "   ";

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientDetailsDto result = clientUpdateService.updateOwnClientDetails(user, nuevoTelefono, emailBlank);

        assertThat(result).isNotNull();
        assertThat(result.telefono()).isEqualTo(nuevoTelefono);
        assertThat(result.email()).isEqualTo("old@email.com");
        verify(clientRepository).save(user.getClient());
    }

    @Test
    void updateOwnClientDetailsWithNullValuesDoesNotUpdateAnything() {
        User user = createUserWithClient("old-phone", "old@email.com");
        String telefonoNull = null;
        String emailNull = null;

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientDetailsDto result = clientUpdateService.updateOwnClientDetails(user, telefonoNull, emailNull);

        assertThat(result).isNotNull();
        assertThat(result.telefono()).isEqualTo("old-phone");
        assertThat(result.email()).isEqualTo("old@email.com");
        verify(clientRepository).save(user.getClient());
    }

    @Test
    void updateOwnClientDetailsWithEmptyStringsDoesNotUpdateAnything() {
        User user = createUserWithClient("old-phone", "old@email.com");
        String telefonoEmpty = "";
        String emailEmpty = "";

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientDetailsDto result = clientUpdateService.updateOwnClientDetails(user, telefonoEmpty, emailEmpty);

        assertThat(result).isNotNull();
        assertThat(result.telefono()).isEqualTo("old-phone");
        assertThat(result.email()).isEqualTo("old@email.com");
        verify(clientRepository).save(user.getClient());
    }

    @Test
    void updateOwnClientDetailsWithUserWithoutClientThrowsValidationException() {
        User user = createUserWithoutClient();
        String nuevoTelefono = "123-456-789";
        String nuevoEmail = "nuevo@email.com";

        assertThrows(ValidationException.class, () -> {
            clientUpdateService.updateOwnClientDetails(user, nuevoTelefono, nuevoEmail);
        });
    }

    @Test
    void updateOwnClientDetailsWithNullUserThrowsNullPointerException() {
        User nullUser = null;
        String telefono = "123-456-789";
        String email = "test@email.com";

        assertThrows(NullPointerException.class, () -> {
            clientUpdateService.updateOwnClientDetails(nullUser, telefono, email);
        });
    }

    @Test
    void updateOwnClientDetailsVerifiesRepositorySaveIsCalled() {
        User user = createUserWithClient("old-phone", "old@email.com");
        String nuevoTelefono = "999-888-777";
        String nuevoEmail = "nuevo@test.com";

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));
        clientUpdateService.updateOwnClientDetails(user, nuevoTelefono, nuevoEmail);
        verify(clientRepository).save(user.getClient());
    }

    @Test
    void updateOwnClientDetailsWithSpecialCharactersInPhoneUpdatesSuccessfully() {
        User user = createUserWithClient("old-phone", "old@email.com");
        String telefonoConCaracteres = "+54-11-1234-5678";
        String nuevoEmail = "test@email.com";

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientDetailsDto result = clientUpdateService.updateOwnClientDetails(user, telefonoConCaracteres, nuevoEmail);

        assertThat(result).isNotNull();
        assertThat(result.telefono()).isEqualTo(telefonoConCaracteres);
        verify(clientRepository).save(user.getClient());
    }

    @Test
    void updateOwnClientDetailsWithLongEmailUpdatesSuccessfully() {
        User user = createUserWithClient("old-phone", "old@email.com");
        String nuevoTelefono = "123-456-789";
        String emailLargo = "usuario.muy.largo.con.muchos.caracteres@dominio.extremadamente.largo.com";

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientDetailsDto result = clientUpdateService.updateOwnClientDetails(user, nuevoTelefono, emailLargo);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(emailLargo);
        verify(clientRepository).save(user.getClient());
    }

    @Test
    void updateOwnClientDetailsMultipleUpdatesWorkCorrectly() {
        User user = createUserWithClient("phone1", "email1@test.com");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientDetailsDto result1 = clientUpdateService.updateOwnClientDetails(user, "phone2", "email2@test.com");
        assertThat(result1.telefono()).isEqualTo("phone2");
        assertThat(result1.email()).isEqualTo("email2@test.com");

        ClientDetailsDto result2 = clientUpdateService.updateOwnClientDetails(user, "phone3", null);
        assertThat(result2.telefono()).isEqualTo("phone3");
        assertThat(result2.email()).isEqualTo("email2@test.com");

        verify(clientRepository, times(2)).save(user.getClient());
    }
}