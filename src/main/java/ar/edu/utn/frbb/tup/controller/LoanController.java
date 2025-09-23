package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.model.client.exceptions.ClientNotFoundException;
import ar.edu.utn.frbb.tup.model.loan.Loan;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanDetailsDto;
import ar.edu.utn.frbb.tup.model.loan.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.loan.dto.LoansListDto;
import ar.edu.utn.frbb.tup.model.loan.exceptions.CreditScoreException;
import ar.edu.utn.frbb.tup.model.loan.exceptions.LoanNotFoundException;
import ar.edu.utn.frbb.tup.model.payment.dto.UpdatePaymentDto;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.service.loan.LoanCreationService;
import ar.edu.utn.frbb.tup.service.loan.LoanPaymentService;
import ar.edu.utn.frbb.tup.service.loan.LoanQueryService;
import ar.edu.utn.frbb.tup.service.loan.LoanStatusService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class LoanController {

    private final LoanCreationService loanService;
    private final LoanQueryService loanQueryService;
    private final LoanPaymentService loanPaymentService;
    private final LoanStatusService loanStatusService;

    //crea el prestamo
    @PostMapping
    public ResponseEntity register(@AuthenticationPrincipal User user, @RequestBody @Valid LoanRequestDto dto, UriComponentsBuilder uriComponentsBuilder)
            throws ClientNotFoundException, CreditScoreException {
        Loan loan = loanService.registerLoan(user, dto);
        var uri = uriComponentsBuilder.path("/api/loans/{id}").buildAndExpand(loan.getId()).toUri();
        return ResponseEntity.created(uri).body(new LoanDetailsDto(loan));
    }

    //busca prestamo por id de prestamo
    @GetMapping("/{id}")
    public ResponseEntity getLoanById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        LoanDetailsDto loan = loanQueryService.findLoanById(user, id);
        return ResponseEntity.ok(loan);
    }

    //busca los prestamos del cliente
    @GetMapping("/client/{dni}")
    @Parameter(name = "pagination", hidden = true)
    public ResponseEntity getLoansByClient(@AuthenticationPrincipal User user, @PathVariable Long dni, @PageableDefault(size = 10) Pageable pagination)
            throws ClientNotFoundException {
        Page<LoansListDto> loans = loanQueryService.findLoansByClient(user, dni, pagination);
        return ResponseEntity.ok(loans);
    }

    //paga cuotas del prestamo
    @PutMapping("/{id}/payment")
    public ResponseEntity payInstallment(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody UpdatePaymentDto dto) throws
            LoanNotFoundException, ClientNotFoundException {
        loanPaymentService.payInstallment(user, id, dto);
        return ResponseEntity.ok().build();
    }

    //marca prestamo como cerrado
    @DeleteMapping("/{id}")
    public ResponseEntity close(@AuthenticationPrincipal User user, @PathVariable Long id) {
        loanStatusService.closeLoan(user, id);
        return ResponseEntity.noContent().build();
    }
}