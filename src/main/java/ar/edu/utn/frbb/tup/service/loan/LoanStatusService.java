package ar.edu.utn.frbb.tup.service.loan;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.loan.exceptions.LoanNotFoundException;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanStatusService {

    private final LoanRepository repository;

    public void closeLoan(User authenticatedUser, Long id) {
        if (authenticatedUser == null) {
            throw new ValidationException("El usuario autenticado no puede ser null.");
        }

        if (id == null) {
            throw new LoanNotFoundException("El ID del préstamo no puede ser null.");
        }

        var loan = repository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Préstamo con ID " + id + " no encontrado."));

        if (!loan.getClient().getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tenés permiso para cerrar este préstamo.");
        }

        loan.getCuotas().forEach(payment -> {
            if (!payment.getPaid()) {
                payment.setPaid(true);
                payment.setPaymentDate(LocalDate.now());
            }
        });
        loan.close();
        repository.save(loan);
    }
}