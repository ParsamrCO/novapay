package com.novapay.ledger.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.novapay.ledger.LedgerEntry;
import com.novapay.ledger.Transaction;

public record TransactionResponse(
    UUID id , 
    String type, 
    String status , 
    String description , 
    String createdAt , 
    UUID reversedByTx , 
    List<Entry> entries
) {
    public record Entry(UUID accountId , String description , BigDecimal amount) {}

    public static TransactionResponse of(Transaction tx, List<LedgerEntry> entries) {
        return new TransactionResponse(
            tx.getId(), tx.getType().name(), tx.getStatus().name(),
            tx.getDescription(), tx.getCreatedAt().toString(),
            tx.getReversedByTx(),
            entries.stream().map(e -> new Entry(
                e.getAccountId(), e.getDirection().name(), e.getAmount()
            )).toList()
        );
    }
}
