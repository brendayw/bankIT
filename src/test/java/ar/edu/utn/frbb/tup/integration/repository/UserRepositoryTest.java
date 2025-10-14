package ar.edu.utn.frbb.tup.integration.repository;

import ar.edu.utn.frbb.tup.config.IntegrationTestBase;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager em;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        em.persist(user);
        em.flush();
    }

    @Test
    void givenExistingUser_whenFindingByUsername_thenReturnUser() {
        User found = (User) repository.findByUsername("testuser");

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("testuser");
    }

    @Test
    void givenNonExistingUser_whenFindingByUsername_thenReturnNull() {
        User found = (User) repository.findByUsername("nonexistent");

        assertThat(found).isNull();
    }
}
