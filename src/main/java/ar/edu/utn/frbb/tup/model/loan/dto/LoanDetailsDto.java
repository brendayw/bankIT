package ar.edu.utn.frbb.tup.model.loan.dto;

import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.payment.dto.PaymentPlanDto;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;

import java.time.LocalDate;

public record LoanDetailsDto(
        Long id,
        Long dni,
        Double totalAmount,
        Integer termInMonths,
        CurrencyType currencyType,
        LoanStatus status,
        LocalDate registrationDate,
        PaymentPlanDto paymentPlan
) {
    public LoanDetailsDto(Loan loan) {
        this(
                loan.getId(),
                loan.getClient().getPerson().getDni(),
                loan.getTotalAmount(),
                loan.getTermInMonths(),
                loan.getCurrencyType(),
                loan.getLoanStatus(),
                loan.getRegistrationDate(),
                new PaymentPlanDto(loan)
        );
    }
}