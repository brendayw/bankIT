package ar.edu.utn.frbb.tup.service.payment;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.payment.Payment;
import ar.edu.utn.frbb.tup.model.payment.dto.PaymentDto;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.PaymentRepository;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private LoanRepository loanRepository;

    public PaymentService(PaymentRepository repository, LoanRepository loanRepository) {
        this.repository = repository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    public Payment createPayment(User authenticatedUser, Loan loan, Integer number, Double amount) {
        Payment payment = new Payment();
        payment.setLoan(loan);
        payment.setPaymentNumber(number);
        payment.setPaymentAmount(amount);
        payment.setPaid(false);
        return repository.save(payment);
    }

    @Transactional
    public void markAsPaid(User authenticatedUser, Long id) throws ValidationException {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new ValidationException("Pago no encontrado con ID: " + id));
        if (!payment.getLoan().getClient().getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tienes permiso para marcar esta cuota como pagada");
        }
        if (payment.getPaid()) {
            throw new ValidationException("La cuota ya está pagada");
        }
        payment.setPaid(true);
        payment.setPaymentDate(LocalDate.now());
        repository.save(payment);
    }

    //consultar pagos
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByLoan(User authenticatedUser, Long id) {
        List<Payment> payments = repository.findByLoanId(id);
        return payments.stream()
                .map(PaymentDto::new)
                .collect(Collectors.toList());
    }

    //calcule el saldo restante a pagar del prestamo
    @Transactional(readOnly = true)
    public double calculatePayment(User authenticatedUser, Long id) throws ValidationException {
        List<Payment> payments = repository.findByLoanId(id);
        return payments.stream()
                .filter(p -> !p.getPaid())
                .mapToDouble(Payment::getPaymentAmount)
                .sum();
    }
}