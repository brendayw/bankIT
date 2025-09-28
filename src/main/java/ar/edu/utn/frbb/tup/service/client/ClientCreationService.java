package ar.edu.utn.frbb.tup.service.client;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDto;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientCreationService {

    private final ClientRepository repository;

    public Client createClient(ClientDto dto, User user) {
        if (repository.existsByPersonDni(dto.person().dni())) {
            throw new ClientAlreadyExistsException("Cliente con DNI " + dto.person().dni() + " ya existe.");
        }
        if (!user.canHaveClient()) {
            throw new ValidationException("El usuario ya tiene un cliente asociado");
        }
        Client client = Client.createFromDto(dto);
        client.associateWithUser(user);
        return repository.save(client);
    }
}
