package ar.edu.utn.frbb.tup.service.loan;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import ar.edu.utn.frbb.tup.service.payment.PaymentPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoanCreationService {
    private final LoanRepository repository;
    private final ClientRepository clientRepository;
    private final LoanSimulatorService simulatorService;
    private final PaymentPlanService paymentPlanService;

    public Loan registerLoan(User authenticatedUser, LoanRequestDto dto) {
        var client = clientRepository.findByPersonDni(dto.dni())
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        if (!client.getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tienes permiso para este cliente");
        }

        Loan loan = simulatorService.createLoan(client, dto);
        Loan savedLoan = repository.save(loan);

        if (savedLoan.getLoanStatus() == LoanStatus.APROBADO) {
            paymentPlanService.createAndAssignPlan(authenticatedUser, savedLoan, dto.termInMonths());
            paymentPlanService.generatePlan(authenticatedUser, savedLoan, savedLoan.getTotalAmount(), savedLoan.getTermInMonths());
        }
        return savedLoan;
    }
}