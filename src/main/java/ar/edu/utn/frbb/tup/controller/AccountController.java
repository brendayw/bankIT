package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.model.account.dto.AccountDetailsDto;
import ar.edu.utn.frbb.tup.model.account.dto.AccountDto;
import ar.edu.utn.frbb.tup.model.account.dto.AccountsListDto;
import ar.edu.utn.frbb.tup.model.users.User;

import ar.edu.utn.frbb.tup.service.AccountService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/cuentas")
@SecurityRequirement(name = "bearer-key")
public class AccountController {

    @Autowired
    private AccountService service;

    //crea cuenta
    @Transactional
    @PostMapping
    public ResponseEntity register(@RequestBody @Valid AccountDto dto, UriComponentsBuilder uriComponentsBuilder) {
        var account = service.createAccount(dto.dniTitular(), dto);
        var uri = uriComponentsBuilder.path("/api/cuenta/{id}").buildAndExpand(account.getId()).toUri();
        return ResponseEntity.created(uri).body(new AccountDetailsDto(account));
    }

    //busca cuenta por numero de cuenta
    @GetMapping("/{id}")
    public ResponseEntity getAccountById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        AccountDetailsDto account = service.getAccountById(id, user);
        return ResponseEntity.ok(account);
    }

    //busca cuenta de cliente por dni de cliente
    @GetMapping("/user")
    @Parameter(name = "pagination", hidden = true)
    public ResponseEntity getAccountsByClient(@AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pagination) {
        AccountsListDto accounts = service.findAllAccountsByAuthenticatedUser(user, pagination);
        return ResponseEntity.ok(accounts);
    }

    //desactiva cuenta
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        service.deactivateAccount(user, id);
        return ResponseEntity.noContent().build();
    }
}