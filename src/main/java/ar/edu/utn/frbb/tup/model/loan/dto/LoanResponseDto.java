package ar.edu.utn.frbb.tup.model.loan.dto;

import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;

import java.time.LocalDate;

public record LoanResponseDto(
        Long id,
        Double totalAmount,
        LoanStatus status,
        CurrencyType currencyType,
        LocalDate registrationDate
) {
    public LoanResponseDto(Loan loan) {
        this(
                loan.getId(),
                loan.getTotalAmount(),
                loan.getLoanStatus(),
                loan.getCurrencyType(),
                loan.getRegistrationDate()
        );
    }
}