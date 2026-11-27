package com.novapay.account;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity 
@Table(name = "accounts")
public class Account {
    @Id 
    @GeneratedValue 
    private UUID id;
    
    @Column(name = "user_id" , nullable = false)
    private UUID userId;

    @Column(nullable = false , length = 100)
    private String name ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false , length = 20)
    private AccountType type;

    @Column(nullable = false, length = 3, columnDefinition = "char(3)")
    private String currency = "IRR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false , length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;


    @Column(name ="updated_at" , nullable = false)
    private Instant updatedAt;

    @Column(name ="created_at" , nullable = false , updatable = false)
    private Instant createdAt;

    @PrePersist 
    public void onCreate(){
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }


    @PreUpdate 
    public void onUpdate(){
        this.updatedAt = Instant.now();
    }


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


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public AccountType getType() {
        return type;
    }


    public void setType(AccountType type) {
        this.type = type;
    }


    public String getCurrency() {
        return currency;
    }


    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public AccountStatus getStatus() {
        return status;
    }


    public void setStatus(AccountStatus status) {
        this.status = status;
    }


    public Instant getUpdatedAt() {
        return updatedAt;
    }


    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }


    public Instant getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    

}
