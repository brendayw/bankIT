package ar.edu.utn.frbb.tup.model.payment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdatePaymentDto(
        @NotNull Long id,
        @NotNull @Min(1000000) @Max(999999999) Long dni,
        LocalDate paymentDate
) {
}
