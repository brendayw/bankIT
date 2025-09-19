package ar.edu.utn.frbb.tup.repository;

import ar.edu.utn.frbb.tup.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByLoanId(Long loanId);
}