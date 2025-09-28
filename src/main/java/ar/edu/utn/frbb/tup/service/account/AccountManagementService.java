package ar.edu.utn.frbb.tup.service.account;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.account.exceptions.AccountNotFoundException;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountManagementService {

    private final AccountRepository repository;

    //desactivar cuenta
    public void deactivateAccount(User authenticatedUser, Long id) {
        var client = authenticatedUser.getClient();
        if (client == null) {
            throw new IllegalStateException("El usuario no tiene cliente asociado");
        }

        var account = repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada o no tenés permiso para acceder."));
        if (!account.getClient().getId().equals(client.getId())) {
            throw new ValidationException("No tenés permiso para desactivar esta cuenta.");
        }
        account.deactivate();
        repository.save(account);
    }
}
