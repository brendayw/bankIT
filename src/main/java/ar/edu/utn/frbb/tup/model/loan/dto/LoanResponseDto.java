package ar.edu.utn.frbb.tup.model.loan.dto;

import ar.edu.utn.frbb.tup.model.account.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;

import java.time.LocalDate;

public record LoanResponseDto(
        Long id,
        Double montoTotal,
        LoanStatus estado,
        TipoMoneda tipoMoneda,
        LocalDate fechaAlta
) {
    public LoanResponseDto(Loan loan) {
        this(
                loan.getId(),
                loan.getMontoTotal(),
                loan.getLoanStatus(),
                loan.getMoneda(),
                loan.getFechaAlta()
        );
    }
}
