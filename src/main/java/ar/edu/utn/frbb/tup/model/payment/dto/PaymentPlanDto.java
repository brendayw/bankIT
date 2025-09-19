package ar.edu.utn.frbb.tup.model.payment.dto;

import ar.edu.utn.frbb.tup.model.loan.Loan;

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