package com.mpvaitheeswaran.ecommerce.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;                       // reference to user-service

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String mobile;

    @Column(name = "password_hash")
    private String passwordHash;               // BCrypt

    @Column(name = "is_email_verified")
    private boolean isEmailVerified = false;

    @Column(name = "is_mobile_verified")
    private boolean isMobileVerified = false;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "is_locked")
    private boolean isLocked = false;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "failed_attempts")
    private int failedAttempts = 0;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void onUpdate() { this.updatedAt = Instant.now(); }
}
