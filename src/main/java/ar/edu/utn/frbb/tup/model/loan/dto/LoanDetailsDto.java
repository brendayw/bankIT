package ar.edu.utn.frbb.tup.model.loan.dto;

import ar.edu.utn.frbb.tup.model.account.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.payment.dto.PaymentPlanDto;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;

import java.time.LocalDate;

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
