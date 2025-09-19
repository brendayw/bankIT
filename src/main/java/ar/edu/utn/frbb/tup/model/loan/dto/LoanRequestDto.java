package ar.edu.utn.frbb.tup.model.loan.dto;

import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LoanRequestDto(
        @NotNull @Min(1000000) @Max(999999999) Long dni,
        @NotNull Double requestedAmount,
        @NotNull @Valid CurrencyType currencyType,
        @NotNull @Valid AccountType accountType,
        @NotNull Integer termInMonths) {
}