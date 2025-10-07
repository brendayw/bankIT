package ar.edu.utn.frbb.tup.service.loan;

import ar.edu.utn.frbb.tup.infra.exception.ValidationException;
import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanDetailsDto;
import ar.edu.utn.frbb.tup.model.loan.dto.LoansListDto;
import ar.edu.utn.frbb.tup.model.loan.exceptions.LoanNotFoundException;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.repository.ClientRepository;
import ar.edu.utn.frbb.tup.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoanQueryService {

    private final LoanRepository repository;
    private final ClientRepository clientRepository;

    public LoanDetailsDto findLoanById(User authenticatedUser, Long id) {
        var loan = repository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Préstamo no encontrado"));

        if (loan.getClient() == null || loan.getClient().getUser() == null) {
            throw new ValidationException("No tenés permiso para este préstamo");
        }

        if (!loan.getClient().getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tenés permiso para este préstamo");
        }
        return new LoanDetailsDto(loan);
    }

    public Page<LoansListDto> findLoansByClient(User authenticatedUser, Long dni, Pageable pagination) {
        var client = clientRepository.findByPersonDni(dni)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        if (client.getUser() == null) {
            throw new ValidationException("No tenés permiso para estos préstamos");
        }

        if (!client.getUser().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("No tenés permiso para estos préstamos");
        }

        Page<Loan> loans = repository.findByClientPersonDni(dni, pagination);
        return loans.map(LoansListDto::new);
    }
}