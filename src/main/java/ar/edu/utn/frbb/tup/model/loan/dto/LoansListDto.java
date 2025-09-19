package ar.edu.utn.frbb.tup.model.loan.dto;

import ar.edu.utn.frbb.tup.model.account.enums.CurrencyType;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;

public record LoansListDto(
        Long id,
        Long dni,
        Double totalAmount,
        CurrencyType currencyType,
        Integer termInMonths,
        LoanStatus status
) {
    public LoansListDto(Loan loan) {
        this(
                loan.getId(),
                loan.getClient().getPerson().getDni(),
                loan.getTotalAmount(),
                loan.getCurrencyType(),
                loan.getTermInMonths(),
                loan.getLoanStatus()
        );
    }
}