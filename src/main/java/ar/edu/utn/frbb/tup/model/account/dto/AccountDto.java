package ar.edu.utn.frbb.tup.model.account.dto;

import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record AccountDto(
        @NotNull @Min(1000000) @Max(999999999) Long dni,
        @NotNull @Valid CurrencyType currencyType,
        @NotNull @Valid AccountType accountType,
        @NotNull @Valid Double balance) {

    public AccountDto(Account account) {
        this(
                account.getClient() != null ? account.getClient().getPerson().getDni() : null,
                account.getCurrencyType(),
                account.getAccountType(),
                account.getBalance()
        );
    }
}