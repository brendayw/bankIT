package ar.edu.utn.frbb.tup.model.payment.dto;

import ar.edu.utn.frbb.tup.model.payment.Payment;

public record PaymentDto(
        Long id,
        Integer paymentNumber,
        Double paymentAmount,
        Boolean paid
) {
    public PaymentDto(Payment payment) {
        this(
                payment.getId(),
                payment.getPaymentNumber(),
                payment.getPaymentAmount(),
                payment.getPaid()
        );
    }
}
