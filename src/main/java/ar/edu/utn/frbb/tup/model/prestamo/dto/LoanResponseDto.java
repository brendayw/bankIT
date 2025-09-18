package ar.edu.utn.frbb.tup.model.prestamo.dto;

import ar.edu.utn.frbb.tup.model.cuenta.TipoMoneda;
import ar.edu.utn.frbb.tup.model.prestamo.Loan;
import ar.edu.utn.frbb.tup.model.prestamo.LoanStatus;

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
