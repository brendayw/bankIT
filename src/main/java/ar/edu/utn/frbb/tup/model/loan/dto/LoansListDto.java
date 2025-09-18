package ar.edu.utn.frbb.tup.model.loan.dto;

import ar.edu.utn.frbb.tup.model.account.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;

public record LoansListDto(
        Long id,
        Long dni,
        Double montoTotal,
        TipoMoneda tipoMoneda,
        Integer plazoMeses,
        LoanStatus estado
) {
    public LoansListDto(Loan loan) {
        this(
                loan.getId(),
                loan.getClient().getPersona().getDni(),
                loan.getMontoTotal(),
                loan.getMoneda(),
                loan.getPlazoMeses(),
                loan.getLoanStatus()
        );
    }
}
