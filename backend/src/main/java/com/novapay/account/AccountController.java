package com.novapay.account;

import com.novapay.account.dto.AccountResponse;
import com.novapay.account.dto.CreateAccountRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(
            Authentication auth,
            @Valid @RequestBody CreateAccountRequest req) {
        return ResponseEntity.status(201).body(accountService.create(uid(auth), req));
    }

    @GetMapping
    public List<AccountResponse> list(Authentication auth) {
        return accountService.list(uid(auth));
    }

    @GetMapping("/{id}")
    public AccountResponse get(Authentication auth, @PathVariable UUID id) {
        return accountService.get(uid(auth), id);
    }

    @PostMapping("/{id}/close")
    public AccountResponse close(Authentication auth, @PathVariable UUID id) {
        return accountService.close(uid(auth), id);
    }

    private UUID uid(Authentication auth) { return (UUID) auth.getPrincipal(); }
}