package com.novapay.ledger.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.novapay.ledger.TransactionType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTransactionRequest(
    @NotNull TransactionType type ,
    @Size(max = 500) String description , 
    @NotNull @Size(min = 2) @Valid List<EntryLine> entries
) {
    public record EntryLine(
        @NotNull UUID accountId , 
        @NotNull String direction , 
        @NotNull @DecimalMin("0.0001") BigDecimal amount
    ) {
    }
}
