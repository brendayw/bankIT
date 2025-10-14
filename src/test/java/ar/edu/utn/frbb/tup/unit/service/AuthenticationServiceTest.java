package ar.edu.utn.frbb.tup.unit.service;

import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.model.users.exceptions.UserNotFoundException;
import ar.edu.utn.frbb.tup.repository.UserRepository;
import ar.edu.utn.frbb.tup.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        String username = "testuser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("encodedPassword");

        when(repository.findByUsername(username)).thenReturn(mockUser);

        UserDetails result = authenticationService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(repository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsUserNotFoundException() {
        String username = "nonexistent";
        when(repository.findByUsername(username)).thenReturn(null);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> authenticationService.loadUserByUsername(username)
        );

        assertEquals("Usuario no encontrado: " + username, exception.getMessage());
        verify(repository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_EmptyUsername_ThrowsException() {
        String emptyUsername = "";
        when(repository.findByUsername(emptyUsername)).thenReturn(null);

        assertThrows(
                UserNotFoundException.class,
                () -> authenticationService.loadUserByUsername(emptyUsername)
        );
    }

    @Test
    void loadUserByUsername_NullUsername_ThrowsException() {
        when(repository.findByUsername(null)).thenReturn(null);

        assertThrows(
                UserNotFoundException.class,
                () -> authenticationService.loadUserByUsername(null)
        );
    }
}