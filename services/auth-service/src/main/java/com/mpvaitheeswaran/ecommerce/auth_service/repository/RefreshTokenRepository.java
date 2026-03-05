package com.mpvaitheeswaran.ecommerce.auth_service.repository;

import com.mpvaitheeswaran.ecommerce.auth_service.model.AuthCredential;
import com.mpvaitheeswaran.ecommerce.auth_service.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying    // Required for DELETE/UPDATE queries
    @Transactional // Required to provide the EntityManager a transaction context
    void deleteAllByCredential(AuthCredential credential);   // used on logout
    void deleteAllByExpiresAtBefore(Instant now);           // cleanup job
}