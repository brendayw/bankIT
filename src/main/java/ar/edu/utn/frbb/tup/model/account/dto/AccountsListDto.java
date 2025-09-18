package ar.edu.utn.frbb.tup.model.account.dto;

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
