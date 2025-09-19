package ar.edu.utn.frbb.tup.service.loan;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.model.payment.dto.UpdatePaymentDto;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanDetailsDto;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.loan.dto.LoansListDto;
import ar.edu.utn.frbb.tup.model.loan.exceptions.LoanNotFoundException;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import ar.edu.utn.frbb.tup.service.payment.PaymentPlanService;
import ar.edu.utn.frbb.tup.service.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LoanService {
    @Autowired
    private LoanRepository repository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LoanSimulatorService simulatorService;

    @Autowired
    private PaymentPlanService paymentPlanService;

    @Autowired
    private PaymentService paymentService;

    //registra el prestamo y crea el plan de pagos
    public Loan registerLoan(User authenticatedUser, LoanRequestDto dto) {
        var client = clientRepository.findByPersonDni(dto.dni())
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado con DNI: " + dto.dni()));
        if (!client.getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tienes permiso para registrar préstamos para este cliente.");
        }
        Loan loan = simulatorService.createLoan(client, dto);
        repository.save(loan);
        if (loan.getLoanStatus() == LoanStatus.APROBADO) {
            paymentPlanService.createAndAssignPlan(authenticatedUser, loan, dto.termInMonths());
            paymentPlanService.generatePlan(authenticatedUser, loan, loan.getTotalAmount(), loan.getTermInMonths());
            repository.save(loan);
        }
        return loan;
    }

    //obtiene prestamo por id del prestamo
    public LoanDetailsDto findLoanById(User authenticatedUser, Long id) {
        var loan = repository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Préstamo con ID " + id + " no encontrado."));
        if (!loan.getClient().getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tenés permiso para acceder a este préstamo.");
        }
        return new LoanDetailsDto(loan);
    }

    //obtener prestamos por dni del cliente
    public Page<LoansListDto> findLoansByClient(User authenticatedUser, Long dni, Pageable pagination) {
        var client = clientRepository.findByPersonDni(dni)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado con DNI: " + dni));
        if (!client.getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tenés permiso para acceder a estos préstamos.");
        }
        Page<Loan> loans = repository.findByClientPersonDni(dni, pagination);
        if (loans.isEmpty()) {
            throw new ClientNotFoundException("El cliente con DNI " + dni + " no tiene préstamos o no existe");
        }
        return loans.map(LoansListDto::new);
    }

    //pagar cuota del prestamo
    public LoansListDto payInstallment(User authenticatedUser, Long id, UpdatePaymentDto dto) {
        var loan = repository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Préstamo con ID " + id + " no encontrado."));
        if (!loan.getClient().getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tenés permiso para pagar cuotas de este préstamo.");
        }
        if (!loan.getClient().getPerson().getDni().equals(dto.dni())) {
            throw new ClientNotFoundException("El DNI del Cliente no coincide con el cliente que solicitó el préstamo.");
        }
        paymentService.markAsPaid(authenticatedUser, dto.id());
        return new LoansListDto(loan);
    }

    //marca el prestamo como cerrado / temrinado de pagar
    public void closeLoan(User authenticatedUser, Long id) {
        var loan = repository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Préstamo con ID " + id + " no encontrado."));

        if (!loan.getClient().getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tenés permiso para cerrar este préstamo.");
        }

        loan.getCuotas().forEach(payment -> {
            if (!payment.getPaid()) {
                payment.setPaid(true);
                payment.setPaymentDate(LocalDate.now());
            }
        });
        loan.close();
        repository.save(loan);
    }
}