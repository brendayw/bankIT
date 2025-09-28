package ar.edu.utn.frbb.tup.service.client;

import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientManagementService {

    private final ClientRepository repository;

    //desactivar cliente
    public void deactivateOwnClient(User authenticatedUser) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new ClientNotFoundException("El usuario no tiene un cliente asociado");
        }
        client.deactivate();
        repository.save(client);
    }
}
