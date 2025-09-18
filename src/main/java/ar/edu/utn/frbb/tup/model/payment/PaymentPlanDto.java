package ar.edu.utn.frbb.tup.model.payment;

import ar.edu.utn.frbb.tup.model.prestamo.Loan;

import java.util.List;
import java.util.stream.Collectors;

public record PaymentPlanDto(
        Long id,
        List<PaymentDto> payments
) {
    public PaymentPlanDto(Loan loan) {
        this(
                loan.getId(),
                loan.getCuotas().stream()
                        .map(PaymentDto::new)
                        .collect(Collectors.toList())
        );
    }
}
