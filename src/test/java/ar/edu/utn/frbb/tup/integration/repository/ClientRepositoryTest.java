package ar.edu.utn.frbb.tup.integration.repository;

import ar.edu.utn.frbb.tup.config.IntegrationTestBase;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.person.Person;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClientRepositoryTest {
    @Autowired
    private ClientRepository repository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        Client client = createClient();
    }

    @Test
    void givenExistingClient_whenFindingByDni_thenReturnClient() {
        Optional<Client> found = repository.findByPersonDni(12345678L);

        assertThat(found).isPresent();
        assertThat(found.get().getPerson().getDni()).isEqualTo(12345678L);
    }

    @Test
    void givenNonExistingClient_whenFindingByDni_thenReturnEmpty() {
        Optional<Client> found = repository.findByPersonDni(99999999L);

        assertThat(found).isEmpty();
    }

    @Test
    void givenExistingClient_whenCheckingExistenceByDni_thenReturnTrue() {
        boolean exists = repository.existsByPersonDni(12345678L);

        assertThat(exists).isTrue();
    }

    @Test
    void givenNonExistingClient_whenCheckingExistenceByDni_thenReturnFalse() {
        boolean exists = repository.existsByPersonDni(99999999L);

        assertThat(exists).isFalse();
    }

    //HELPERS
    private Client createClient() {
        Person person = Person.builder()
                .dni(12345678L)
                .nombre("NombreTest")
                .apellido("ApellidoTest")
                .email("testuse@email.com")
                .telefono("2915748896")
                .fechaNacimiento(LocalDate.now().minusYears(30))
                .build();

        Client client = Client.builder()
                .person(person)
                .registrationDate(LocalDate.now())
                .build();
        em.persist(client);
        return client;
    }

}