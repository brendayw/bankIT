package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.model.users.dto.AuthenticationDto;
import ar.edu.utn.frbb.tup.model.users.dto.TokenJWTDto;
import ar.edu.utn.frbb.tup.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager manager;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnTokenWhenLoginIsSuccessful() {
        AuthenticationDto dto = new AuthenticationDto("user@test.com", "password123");

        User user = new User();
        user.setUsername(dto.username());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenService.generateToken(user)).thenReturn("mocked-jwt-token");

        ResponseEntity<?> response = authenticationController.login(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(TokenJWTDto.class);
        TokenJWTDto tokenResponse = (TokenJWTDto) response.getBody();
        assertThat(tokenResponse.token()).isEqualTo("mocked-jwt-token");

        verify(manager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).generateToken(user);
    }
}