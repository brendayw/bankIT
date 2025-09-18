package ar.edu.utn.frbb.tup.service.loans;

import ar.edu.utn.frbb.tup.model.cliente.Client;
import ar.edu.utn.frbb.tup.model.prestamo.Loan;
import ar.edu.utn.frbb.tup.model.prestamo.LoanStatus;
import ar.edu.utn.frbb.tup.model.prestamo.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.prestamo.exceptions.CreditScoreException;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import ar.edu.utn.frbb.tup.repository.PaymentPlanRepository;
import ar.edu.utn.frbb.tup.service.payments.PaymentService;
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
        int score = creditScoreService.calculateScore(new HashSet<>(client.getPrestamos()));
        System.out.println("[INFO] Score calculado para el cliente " + client.getPersona().getDni() + ": " + score);

        Loan loan = new Loan();
        loan.setClient(client);
        loan.setMontoSolicitado(request.montoSolicitado());
        loan.setPlazoMeses(request.plazoMeses());
        loan.setMoneda(request.tipoMoneda());
        loan.setCuenta(request.tipoCuenta());
        loan.setFechaAlta(LocalDate.now());

        if (score < 600) {
            loan.setLoanStatus(LoanStatus.RECHAZADO);
            loan.setMontoTotal(0.0);
            return loan;
        } else {
            loan.setLoanStatus(LoanStatus.APROBADO);
            Double interes = calculateRate(request.montoSolicitado(), request.plazoMeses());
            loan.setInteres(interes);
            loan.setMontoTotal(request.montoSolicitado() + interes);
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
