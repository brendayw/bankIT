package ar.edu.utn.frbb.tup.unit.service;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.service.TokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    private TokenService tokenService;
    private final String secret = "mi-token-super-seguro-que-debe-ser-muy-largo-para-que-funcione";

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", secret);
    }

    @Test
    void generateToken_WithValidUser_ReturnsValidToken() {
        User user = new User();
        user.setUsername("john.doe");

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String subject = tokenService.getSubject(token);
        assertEquals("john.doe", subject);
    }

    @Test
    void generateToken_TokenContainsCorrectClaims() {
        User user = new User();
        user.setUsername("jane.doe");

        String token = tokenService.generateToken(user);

        var decodedJWT = JWT.decode(token);
        assertEquals("API bankIT", decodedJWT.getIssuer());
        assertEquals("jane.doe", decodedJWT.getSubject());
        assertTrue(decodedJWT.getExpiresAt().toInstant().isAfter(Instant.now()));
    }

    @Test
    void getSubject_WithValidToken_ReturnsUsername() {
        User user = new User();
        user.setUsername("test.user");
        String token = tokenService.generateToken(user);

        String subject = tokenService.getSubject(token);

        assertEquals("test.user", subject);
    }

    @Test
    void getSubject_WithExpiredToken_ThrowsException() {
        String expiredToken = JWT.create()
                .withIssuer("API bankIT")
                .withSubject("expired.user")
                .withExpiresAt(Instant.now().minusSeconds(3600)) // 1 hora en el pasado
                .sign(Algorithm.HMAC256(secret));

        assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject(expiredToken);
        });
    }

    @Test
    void getSubject_WithInvalidSignature_ThrowsException() {
        String tokenWithDifferentSecret = JWT.create()
                .withIssuer("API bankIT")
                .withSubject("test.user")
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256("different-secret"));

        assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject(tokenWithDifferentSecret);
        });
    }

    @Test
    void getSubject_WithMalformedToken_ThrowsException() {
        String malformedToken = "malformed.token.string";

        assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject(malformedToken);
        });
    }

    @Test
    void generateToken_WithNullUser_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenService.generateToken(null);
        });

        assertEquals("El usuario no puede ser nulo", exception.getMessage());
    }

    @Test
    void generateToken_WithUserNullUsername_ThrowsException() {
        User user = new User();
        user.setUsername(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenService.generateToken(user);
        });

        assertEquals("El username no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void generateToken_WithUserEmptyUsername_ThrowsException() {
        User user = new User();
        user.setUsername("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenService.generateToken(user);
        });

        assertEquals("El username no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void getSubject_WithNullToken_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenService.getSubject(null);
        });

        assertEquals("El token JWT no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void getSubject_WithEmptyToken_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenService.getSubject("");
        });

        assertEquals("El token JWT no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void getSubject_WithBlankToken_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenService.getSubject("   ");
        });

        assertEquals("El token JWT no puede ser nulo o vacío", exception.getMessage());
    }
}