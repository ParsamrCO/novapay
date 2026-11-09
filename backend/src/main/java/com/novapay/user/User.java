package com.novapay.user;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity 
@Table(name ="users")
public class User {
    @Id 
    @GeneratedValue
    private UUID id;

    @Column(nullable = false , unique = true)
    private String email;
    @Column(name = "password_hash , nullable = false")
    private String passwordHash;
    @Column(name ="full_name" , nullable = false , length = 120)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false , length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "created_at" , nullable = false , updatable = false)
    private Instant createdAt;
    @Column(name ="updated_at" , nullable = false)
    private Instant updateAt;

    @PrePersist 
    void onCreate(){
        Instant now =Instant.now();
        this.createdAt = now;
        this.updateAt = now;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }

    @PreUpdate 
    void onUpdate(){
        this.updateAt = Instant.now();
    }


    

}
