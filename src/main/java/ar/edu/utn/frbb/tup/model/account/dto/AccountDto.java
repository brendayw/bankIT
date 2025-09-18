package ar.edu.utn.frbb.tup.model.account.dto;

import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.account.enums.TipoMoneda;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record AccountDto(
        @NotNull @Min(1000000) @Max(999999999) Long dniTitular,
        @NotNull @Valid TipoMoneda tipoMoneda,
        @NotNull @Valid TipoCuenta tipoCuenta,
        @NotNull @Valid Double balance) {

    public AccountDto(Account account) {
        this(
                account.getClient() != null ? account.getClient().getPersona().getDni() : null,
                account.getTipoMoneda(),
                account.getTipoCuenta(),
                account.getBalance()
        );
    }
}
