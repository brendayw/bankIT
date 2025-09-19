package ar.edu.utn.frbb.tup.repository;

import ar.edu.utn.frbb.tup.model.payment.PaymentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentPlanRepository extends JpaRepository<PaymentPlan, Long> {
}