package ar.edu.utn.frbb.tup.model.account.dto;

import ar.edu.utn.frbb.tup.model.account.Account;
import ar.edu.utn.frbb.tup.model.account.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.account.enums.TipoMoneda;

public record AccountDetailsDto(
        Long id,
        Double balance,
        TipoCuenta tipoCuenta,
        TipoMoneda tipoMoneda
) {
    public AccountDetailsDto(Account account) {
        this(
                account.getId(),
                account.getBalance(),
                account.getTipoCuenta(),
                account.getTipoMoneda()
        );
    }
}
