package com.mpvaitheeswaran.ecommerce.auth_service.repository;

import com.mpvaitheeswaran.ecommerce.auth_service.model.AuthCredential;
import com.mpvaitheeswaran.ecommerce.auth_service.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    void deleteAllByCredential(AuthCredential credential);
}
