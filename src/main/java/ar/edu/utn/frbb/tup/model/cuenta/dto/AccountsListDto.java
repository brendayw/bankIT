package ar.edu.utn.frbb.tup.model.cuenta.dto;

import ar.edu.utn.frbb.tup.model.cuenta.Account;
import ar.edu.utn.frbb.tup.model.cuenta.TipoCuenta;
import ar.edu.utn.frbb.tup.model.cuenta.TipoMoneda;

import java.time.LocalDate;
import java.util.List;

public record AccountsListDto(
        List<AccountDto> accounts
) {
//    public AccountsListDto(Account account) {
//        this(
//                account.getId(),
//                account.getBalance(),
//                account.getFechaCreacion(),
//                account.getTipoCuenta(),
//                account.getTipoMoneda()
//        );
//    }
}
