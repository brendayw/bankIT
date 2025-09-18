package ar.edu.utn.frbb.tup.model.cuenta.dto;

import ar.edu.utn.frbb.tup.model.cuenta.Account;
import ar.edu.utn.frbb.tup.model.cuenta.TipoCuenta;
import ar.edu.utn.frbb.tup.model.cuenta.TipoMoneda;

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
