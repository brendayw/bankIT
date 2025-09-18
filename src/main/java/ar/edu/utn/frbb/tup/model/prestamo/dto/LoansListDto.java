package ar.edu.utn.frbb.tup.model.prestamo.dto;

import ar.edu.utn.frbb.tup.model.cuenta.TipoMoneda;
import ar.edu.utn.frbb.tup.model.prestamo.Loan;
import ar.edu.utn.frbb.tup.model.prestamo.LoanStatus;

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
