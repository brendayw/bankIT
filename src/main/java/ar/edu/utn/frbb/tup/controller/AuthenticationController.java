package ar.edu.utn.frbb.tup.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import ar.edu.utn.frbb.tup.model.users.AuthenticationDto;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.model.users.TokenJWTDto;
import ar.edu.utn.frbb.tup.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager manager;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDto dto) {
        var authToken = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
        var authentication = manager.authenticate(authToken);
        var tokenJWT = tokenService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new TokenJWTDto(tokenJWT));
    }

}
