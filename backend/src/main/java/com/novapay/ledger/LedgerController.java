package com.novapay.ledger;

import com.novapay.ledger.dto.CreateTransactionRequest;
import com.novapay.ledger.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class LedgerController {

    private final LedgerService ledgerService;
    private final TransactionRepository txRepo;
    private final LedgerEntryRepository entryRepo;

    public LedgerController(LedgerService ledgerService,
                            TransactionRepository txRepo,
                            LedgerEntryRepository entryRepo) {
        this.ledgerService = ledgerService;
        this.txRepo = txRepo;
        this.entryRepo = entryRepo;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> post(
            Authentication auth,
            @Valid @RequestBody CreateTransactionRequest req) {
        UUID userId = (UUID) auth.getPrincipal();

        List<LedgerService.EntrySpec> specs = req.entries().stream()
            .map(e -> new LedgerService.EntrySpec(
                e.accountId(),
                Direction.valueOf(e.direction()),
                e.amount()))
            .toList();

        Transaction tx = ledgerService.postTransaction(
            userId, req.type(), req.description(), specs);

        return ResponseEntity.status(201).body(
            TransactionResponse.of(tx, entryRepo.findByTransactionId(tx.getId())));
    }

    @GetMapping
    public List<TransactionResponse> list(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        return txRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(t -> TransactionResponse.of(t, entryRepo.findByTransactionId(t.getId())))
            .toList();
    }

    @GetMapping("/{id}")
    public TransactionResponse get(Authentication auth, @PathVariable UUID id) {
        Transaction t = txRepo.findById(id)
            .orElseThrow(() -> new IllegalStateException("TX_NOT_FOUND"));
        if (!t.getUserId().equals((UUID) auth.getPrincipal())) {
            throw new IllegalStateException("TX_FORBIDDEN");
        }
        return TransactionResponse.of(t, entryRepo.findByTransactionId(id));
    }

    @PostMapping("/{id}/reverse")
    public TransactionResponse reverse(Authentication auth, @PathVariable UUID id) {
        UUID userId = (UUID) auth.getPrincipal();
        Transaction reversal = ledgerService.reverse(userId, id);
        return TransactionResponse.of(reversal,
            entryRepo.findByTransactionId(reversal.getId()));
    }
}