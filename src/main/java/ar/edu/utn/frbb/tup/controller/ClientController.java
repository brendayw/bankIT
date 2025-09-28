package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.model.client.dto.ClientDetailsDto;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDto;
import ar.edu.utn.frbb.tup.model.client.dto.UpdateClientDto;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.loan.dto.LoansListDto;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.service.client.ClientCreationService;
import ar.edu.utn.frbb.tup.service.client.ClientManagementService;
import ar.edu.utn.frbb.tup.service.client.ClientQueryService;
import ar.edu.utn.frbb.tup.service.client.ClientUpdateService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class ClientController {

    private final ClientCreationService creationService;
    private final ClientQueryService queryService;
    private final ClientUpdateService updateService;
    private final ClientManagementService managementService;

    //crea cliente
    @PostMapping
    public ResponseEntity register(@RequestBody @Valid ClientDto dto, UriComponentsBuilder uriComponentsBuilder)  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        var client = creationService.createClient(dto, user);
        var uri = uriComponentsBuilder.path("/api/clients/{id}").buildAndExpand(client.getId()).toUri();
        return ResponseEntity.created(uri).body(new ClientDetailsDto(client));
    }

    //busca cliente autenticado
    @GetMapping("/me")
    public ResponseEntity<ClientDetailsDto> getClientProfile(@AuthenticationPrincipal User user) {
        ClientDetailsDto client = queryService.getOwnClientProfile(user);
        return ResponseEntity.ok(client);
    }

    //obtener todas las cuentas de un cliente por su dni
    @GetMapping("/me/loans")
    @Parameter(name = "pagination", hidden = true)
    public ResponseEntity getAllLoansByClient(@AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pagination) {
        Page<LoansListDto> loans = queryService.findAllLoansByAuthenticatedClient(user, pagination);
        return ResponseEntity.ok(loans);
    }

    //obtener todos los prestamos de un cliente por su dni
    @GetMapping("/me/accounts")
    @Parameter(name = "pagination", hidden = true)
    public ResponseEntity getAllAccountsByClient(@AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pagination) {
        Page<AccountDto> accounts = queryService.findAllAccountsByAuthenticatedClient(user, pagination);
        return ResponseEntity.ok(accounts);
    }

    //update datos del cliente
    @PutMapping("/me")
    public ResponseEntity updateClient(@AuthenticationPrincipal User user, @RequestBody @Valid UpdateClientDto dto) {
        ClientDetailsDto updated = updateService.updateOwnClientDetails(user, dto.telefono(), dto.email());
        return ResponseEntity.ok(updated);
    }

    //desactiva cliente
    @DeleteMapping("/me")
    public ResponseEntity delete(@AuthenticationPrincipal User user) {
        managementService.deactivateOwnClient(user);
        return ResponseEntity.noContent().build();
    }
}