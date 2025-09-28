package ar.edu.utn.frbb.tup.service.client;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDetailsDto;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientUpdateService {

    private final ClientRepository repository;

    //update cliente
    public ClientDetailsDto updateOwnClientDetails(User authenticatedUser, String telefono, String email) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new ValidationException("El usuario no tiene un cliente asociado");
        }
        if (telefono != null && !telefono.isBlank()) {
            client.getPerson().setTelefono(telefono);
        }
        if (email != null && !email.isBlank()) {
            client.getPerson().setEmail(email);
        }
        repository.save(client);
        return new ClientDetailsDto(client);
    }
}
