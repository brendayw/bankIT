package ar.edu.utn.frbb.tup.service.loan;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.loan.exceptions.LoanNotFoundException;
import ar.edu.utn.frbb.tup.model.payment.dto.UpdatePaymentDto;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import ar.edu.utn.frbb.tup.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoanPaymentService {

    private final LoanRepository repository;
    private final PaymentService paymentService;

    public void payInstallment(User authenticatedUser, Long loanId, UpdatePaymentDto dto) {
        var loan = repository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Préstamo no encontrado"));

        if (!loan.getClient().getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tenés permiso para pagar este préstamo");
        }
        paymentService.markAsPaid(authenticatedUser, dto.id());
    }
}