package com.novapay.ledger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {

    List<LedgerEntry> findByTransactionId(UUID transactionId);

    
    @Query("""
        SELECT COALESCE(SUM(CASE WHEN e.direction = 'D' THEN e.amount ELSE 0 END), 0)
            - COALESCE(SUM(CASE WHEN e.direction = 'C' THEN e.amount ELSE 0 END), 0)
        FROM LedgerEntry e
        WHERE e.accountId = :accountId
    """)
    BigDecimal rawBalance(@Param("accountId") UUID accountId);
}