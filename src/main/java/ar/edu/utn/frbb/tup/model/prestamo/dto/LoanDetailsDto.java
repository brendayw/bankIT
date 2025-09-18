package ar.edu.utn.frbb.tup.model.prestamo.dto;

import ar.edu.utn.frbb.tup.model.cuenta.TipoMoneda;
import ar.edu.utn.frbb.tup.model.payment.PaymentPlanDto;
import ar.edu.utn.frbb.tup.model.prestamo.Loan;
import ar.edu.utn.frbb.tup.model.prestamo.LoanStatus;
import ar.edu.utn.frbb.tup.model.payment.PaymentDto;

import java.time.LocalDate;
import java.util.List;

public record LoanDetailsDto(
        Long id,
        Long dni,
        Double montoTotal,
        Integer plazoMeses,
        TipoMoneda tipoMoneda,
        LoanStatus estado,
        LocalDate fechaAlta,
        PaymentPlanDto planDePagos
) {
    public LoanDetailsDto(Loan loan) {
        this(
                loan.getId(),
                loan.getClient().getPersona().getDni(),
                loan.getMontoTotal(),
                loan.getPlazoMeses(),
                loan.getMoneda(),
                loan.getLoanStatus(),
                loan.getFechaAlta(),
                new PaymentPlanDto(loan)
        );
    }
}
