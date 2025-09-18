package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.model.client.dto.ClientDetailsDto;
import ar.edu.utn.frbb.tup.model.client.dto.ClientDto;
import ar.edu.utn.frbb.tup.model.client.dto.UpdateClientDto;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.loan.dto.LoansListDto;
import ar.edu.utn.frbb.tup.model.users.User;
import ar.edu.utn.frbb.tup.service.ClientService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/clientes")
@SecurityRequirement(name = "bearer-key")
public class ClientController {

    @Autowired
    private ClientService service;

    //crea cliente
    @Transactional
    @PostMapping
    public ResponseEntity register(@RequestBody @Valid ClientDto dto, UriComponentsBuilder uriComponentsBuilder)  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        var client = service.createClient(dto, user);
        var uri = uriComponentsBuilder.path("/api/clientes/{id}").buildAndExpand(client.getId()).toUri();
        return ResponseEntity.created(uri).body(new ClientDetailsDto(client));
    }

    //obtiene todos los clientes
//    @GetMapping
//    @Parameter(name = "pagination", hidden = true)
//    public ResponseEntity findAllClients(@PageableDefault(size = 10) Pageable pagination) {
//        Page<ClientsListDto> clients = service.findAllClients(pagination);
//        return ResponseEntity.ok(clients);
//    }

    //busca cliente autenticado
    @GetMapping("/me")
    public ResponseEntity<ClientDetailsDto> getClienteProfile(@AuthenticationPrincipal User user) {
        ClientDetailsDto client = service.getOwnClientProfile(user);
        return ResponseEntity.ok(client);
    }

    //obtener todas las cuentas de un cliente por su dni
    @GetMapping("/me/prestamos")
    @Parameter(name = "pagination", hidden = true)
    public ResponseEntity getAllLoansByClient(@AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pagination) {
        Page<LoansListDto> loans = service.findAllLoansByAuthenticatedClient(user, pagination);
        return ResponseEntity.ok(loans);
    }

    //obtener todos los prestamos de un cliente por su dni
    @GetMapping("/me/cuentas")
    @Parameter(name = "pagination", hidden = true)
    public ResponseEntity getAllAccountsByClient(@AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pagination) {
        Page<AccountDto> accounts = service.findAllAccountsByAuthenticatedClient(user, pagination);
        return ResponseEntity.ok(accounts);
    }

    //update datos del cliente
    @Transactional
    @PutMapping("/me")
    public ResponseEntity updateClient(@AuthenticationPrincipal User user, @RequestBody @Valid UpdateClientDto dto) {
        ClientDetailsDto updated = service.updateOwnClientDetails(user, dto.telefono(), dto.email());
        return ResponseEntity.ok(updated);
    }

    //desactiva cliente
    @Transactional
    @DeleteMapping("/me")
    public ResponseEntity desactivar(@AuthenticationPrincipal User user) {
        service.deactivateOwnClient(user);
        return ResponseEntity.noContent().build();
    }
}
