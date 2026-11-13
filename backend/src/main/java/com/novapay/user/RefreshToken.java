package com.novapay.user;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity 
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id 
    @GeneratedValue 
    private UUID id ;

    @Column(name = "user_id" , nullable = false )  
    private UUID userId ;

    @Column(name = "token_hash" , nullable = false , unique = true)
    private String tokenHash;

    @Column(nullable = false)
    private boolean revoked = false ;
    @Column(name = "created_at" , nullable = false , updatable = false) 
    private Instant createdAt;

    @Column(name = "expires_at" , nullable = false)
    private Instant expiresAt;

    @PrePersist 
    void onCreate(){
        this.createdAt = Instant.now();
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

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    

}
