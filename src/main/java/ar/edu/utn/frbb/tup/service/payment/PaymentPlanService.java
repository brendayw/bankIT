package ar.edu.utn.frbb.tup.service.payment;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.payment.Payment;
import ar.edu.utn.frbb.tup.model.payment.PaymentPlan;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import ar.edu.utn.frbb.tup.repository.PaymentPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentPlanService {

    private final PaymentPlanRepository repository;
    private final LoanRepository loanRepository;
    private final PaymentService paymentService;

    //genera el plan de pago
    public List<Payment> generatePlan(User authenticatedUser, Loan loan, Double total, Integer months) {
        if (!loan.getClient().getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tienes permiso para registrar préstamos para este cliente.");
        }
        List<Payment> payments = new ArrayList<>();
        Double amountPerPayment = total / months;
        for (int i = 1; i <= months; i++) {
            Payment payment = paymentService.createPayment(authenticatedUser, loan, i, amountPerPayment);
            loan.getCuotas().add(payment);
            payments.add(payment);
        }
        loanRepository.save(loan);
        return payments;
    }

    public void createAndAssignPlan(User authenticatedUser, Loan loan, Integer months) {
        if (!loan.getClient().getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tiene permiso para crear una plan de pago para este préstamo");
        }
        PaymentPlan plan = new PaymentPlan();
        plan.setInstallments(months);
        if (loan.getRequestedAmount() == null || loan.getRequestedAmount() <= 0) {
            throw new ValidationException("Monto solicitado inválido");
        }
        plan.setInterestRate(loan.getInteres() / loan.getRequestedAmount());
        plan.setFixedAmount(true);
        repository.save(plan);
        loan.setPaymentPlan(plan);
        loanRepository.save(loan);
    }
}