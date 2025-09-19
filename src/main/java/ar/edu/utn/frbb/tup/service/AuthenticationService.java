package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.users.exceptions.UserNotFoundException;
import ar.edu.utn.frbb.tup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        var user = repository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }
        return user;
    }
}