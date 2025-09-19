package ar.edu.utn.frbb.tup.service.loan;

import ar.edu.utn.frbb.tup.model.client.Client;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.loan.exceptions.CreditScoreException;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;

@Service
public class LoanSimulatorService {

    @Autowired
    private  ClientRepository clientRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CreditScoreService creditScoreService;

    public LoanSimulatorService(ClientRepository clientRepository, LoanRepository loanRepository,
                                CreditScoreService creditScoreService) {
        this.clientRepository = clientRepository;
        this.loanRepository = loanRepository;
        this.creditScoreService = creditScoreService;
    }

    //crear el loan
    public Loan createLoan(Client client, LoanRequestDto request) throws CreditScoreException {
        int score = creditScoreService.calculateScore(new HashSet<>(client.getLoans()));
        //System.out.println("[INFO] Score calculado para el cliente " + client.getPerson().getDni() + ": " + score);

        Loan loan = new Loan();
        loan.setClient(client);
        loan.setRequestedAmount(request.requestedAmount());
        loan.setTermInMonths(request.termInMonths());
        loan.setCurrencyType(request.currencyType());
        loan.setAccountType(request.accountType());
        loan.setRegistrationDate(LocalDate.now());

        if (score < 600) {
            loan.setLoanStatus(LoanStatus.RECHAZADO);
            loan.setTotalAmount(0.0);
            return loan;
        } else {
            loan.setLoanStatus(LoanStatus.APROBADO);
            Double interes = calculateRate(request.requestedAmount(), request.termInMonths());
            loan.setInteres(interes);
            loan.setTotalAmount(request.requestedAmount() + interes);
        }
        return loan;
    }

    public Double calculateRate(Double amount, Integer months) {
        final double annualRate = 0.40; // 40%
        final double monthlyRate = annualRate / 12;
        double interest = amount * monthlyRate * months;
        return interest;
    }
}