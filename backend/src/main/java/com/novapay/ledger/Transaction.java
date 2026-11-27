package com.novapay.ledger;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity 
@Table(name = "transactions")
public class Transaction {
    @Id @GeneratedValue 
    private UUID id ; 

    @Column(name = "user_id" , nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false , length = 30)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false , length = 20)
    private TransactionStatus status = TransactionStatus.POSTED;

    @Column(length = 500)
    private String description;

    @Column(name = "reversed_by_tx")
    private UUID reversedByTx;

    @Column(name = "created_at" , nullable = false , updatable = false)
    private Instant createdAt ; 

    @PrePersist public void onCreate(){this.createdAt = Instant.now();}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getReversedByTx() {
        return reversedByTx;
    }

    public void setReversedByTx(UUID reversedByTx) {
        this.reversedByTx = reversedByTx;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    
}
