package ar.edu.utn.frbb.tup.model.account.dto;

import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.enums.AccountType;
import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;

public record AccountDetailsDto(
        Long id,
        Double balance,
        AccountType accountType,
        CurrencyType currencyType
) {
    public AccountDetailsDto(Account account) {
        this(
                account.getId(),
                account.getBalance(),
                account.getAccountType(),
                account.getCurrencyType()
        );
    }
}