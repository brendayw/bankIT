package ar.edu.utn.frbb.tup.service.client;

import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientManagementServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientManagementService clientManagementService;

    private User createUserWithClient(boolean active) {
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
                        .build())
                .active(active)
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
    void deactivateOwnClientWithActiveClientDeactivatesSuccessfully() {
        User user = createUserWithClient(true);
        Client client = user.getClient();

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        clientManagementService.deactivateOwnClient(user);

        assertThat(client.isActive()).isFalse();
        verify(clientRepository).save(client);
    }

    @Test
    void deactivateOwnClientWithAlreadyInactiveClientDeactivatesAgain() {
        User user = createUserWithClient(false);
        Client client = user.getClient();

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        clientManagementService.deactivateOwnClient(user);

        assertThat(client.isActive()).isFalse();
        verify(clientRepository).save(client);
    }

    @Test
    void deactivateOwnClientWithUserWithoutClientThrowsClientNotFoundException() {
        User user = createUserWithoutClient();

        assertThrows(ClientNotFoundException.class, () -> {
            clientManagementService.deactivateOwnClient(user);
        });
    }

    @Test
    void deactivateOwnClientWithNullUserThrowsException() {
        User nullUser = null;

        assertThrows(NullPointerException.class, () -> {
            clientManagementService.deactivateOwnClient(nullUser);
        });
    }

    @Test
    void deactivateOwnClientVerifiesClientRepositorySaveIsCalled() {
        User user = createUserWithClient(true);
        Client client = user.getClient();

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        clientManagementService.deactivateOwnClient(user);

        verify(clientRepository).save(client);
    }

    @Test
    void deactivateOwnClientWithMultipleCallsHandlesCorrectly() {
        User user = createUserWithClient(true);
        Client client = user.getClient();

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        clientManagementService.deactivateOwnClient(user);

        assertThat(client.isActive()).isFalse();
        verify(clientRepository, times(1)).save(client);

        clientManagementService.deactivateOwnClient(user);

        assertThat(client.isActive()).isFalse();
        verify(clientRepository, times(2)).save(client);
    }

    @Test
    void deactivateOwnClientWithClientHavingRelationshipsDeactivatesSuccessfully() {
        User user = createUserWithClient(true);
        Client client = user.getClient();

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        clientManagementService.deactivateOwnClient(user);

        assertThat(client.isActive()).isFalse();
        verify(clientRepository).save(client);
    }

    @Test
    void deactivateOwnClientAfterClientModificationDeactivatesCorrectly() {
        User user = createUserWithClient(true);
        Client client = user.getClient();
        client.getPerson().setTelefono("123456789");

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        clientManagementService.deactivateOwnClient(user);

        assertThat(client.isActive()).isFalse();
        assertThat(client.getPerson().getTelefono()).isEqualTo("123456789");
        verify(clientRepository).save(client);
    }
}
