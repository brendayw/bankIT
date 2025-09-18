package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.model.cliente.exceptions.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.prestamo.Loan;
import ar.edu.utn.frbb.tup.model.prestamo.dto.LoanDetailsDto;
import ar.edu.utn.frbb.tup.model.prestamo.dto.LoanRequestDto;
import ar.edu.utn.frbb.tup.model.prestamo.dto.LoansListDto;
import ar.edu.utn.frbb.tup.model.prestamo.exceptions.CreditScoreException;
import ar.edu.utn.frbb.tup.model.prestamo.exceptions.PrestamoNoExisteException;
import ar.edu.utn.frbb.tup.model.payment.UpdatePaymentDto;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.service.loans.LoanService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/api/prestamos")
@SecurityRequirement(name = "bearer-key")
public class LoanController {

    @Autowired
    private LoanService loanService;

    //crea el prestamo
    @Transactional
    @PostMapping
    public ResponseEntity register(@AuthenticationPrincipal User user, @RequestBody @Valid LoanRequestDto dto, UriComponentsBuilder uriComponentsBuilder)
            throws ClientNoExisteException, CreditScoreException {
        Loan loan = loanService.registerLoan(user, dto);
        var uri = uriComponentsBuilder.path("/api/prestamos/{id}").buildAndExpand(loan.getId()).toUri();
        return ResponseEntity.created(uri).body(new LoanDetailsDto(loan));
    }

    //busca prestamo por id de prestamo
    @GetMapping("/{id}")
    public ResponseEntity getLoanById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        LoanDetailsDto loan = loanService.getLoanById(user, id);
        return ResponseEntity.ok(loan);
    }

    //busca los prestamos del cliente
    @GetMapping("/client/{dni}")
    @Parameter(name = "pagination", hidden = true)
    public ResponseEntity findLoansByClient(@AuthenticationPrincipal User user, @PathVariable Long dni, @PageableDefault(size = 10) Pageable pagination)
            throws ClientNoExisteException {
        Page<LoansListDto> loans = loanService.findLoansByClient(user, dni, pagination);
        return ResponseEntity.ok(loans);
    }

    //paga cuotas del prestamo
    @Transactional
    @PutMapping("/{id}/pagar")
    public ResponseEntity pagarCuota(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody UpdatePaymentDto dto) throws
            PrestamoNoExisteException, ClientNoExisteException {
        loanService.pagarCuota(user, id, dto);
        return ResponseEntity.ok().build();
    }

    //marca prestamo como cerrado
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity close(@AuthenticationPrincipal User user, @PathVariable Long id) {
        loanService.closeLoan(user, id);
        return ResponseEntity.noContent().build();
    }
}
