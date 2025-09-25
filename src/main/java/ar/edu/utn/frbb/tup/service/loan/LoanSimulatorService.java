package ar.edu.utn.frbb.tup.service.loan;

import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.loan.exceptions.CreditScoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class LoanSimulatorService {

    private final CreditScoreService creditScoreService;
    private static final double ANNUAL_RATE = 0.40; // 40%
    private static final int MIN_APPROVAL_SCORE = 500;

    //crear el loan
    @Transactional
    public Loan createLoan(Client client, LoanRequestDto request) throws CreditScoreException {
        int score = creditScoreService.calculateScore(new HashSet<>(client.getLoans()));
        //System.out.println("[INFO] Score calculado para el cliente " + client.getPerson().getDni() + ": " + score);

        Loan.LoanBuilder loanBuilder = Loan.builder()
                .requestedAmount(request.requestedAmount())
                .termInMonths(request.termInMonths())
                .currencyType(request.currencyType())
                .accountType(request.accountType())
                .registrationDate(LocalDate.now())
                .cuotas(new ArrayList<>())
                .paymentPlan(null);

        Loan loan;
        if (score < MIN_APPROVAL_SCORE) {
            loan = loanBuilder
                    .loanStatus(LoanStatus.RECHAZADO)
                    .totalAmount(0.0)
                    .interes(0.0)
                    .build();
        } else {
            Double interes = calculateRate(request.requestedAmount(), request.termInMonths());
            loan = loanBuilder
                    .loanStatus(LoanStatus.APROBADO)
                    .interes(interes)
                    .totalAmount(request.requestedAmount() + interes)
                    .build();
        }
        return loan;
    }

    public Double calculateRate(Double amount, Integer months) {
        double monthlyRate = ANNUAL_RATE / 12;
        double interest = amount * monthlyRate * months;
        return interest;
    }
}