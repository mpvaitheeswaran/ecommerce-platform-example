package com.mpvaitheeswaran.ecommerce.auth_service.repository;

import com.mpvaitheeswaran.ecommerce.auth_service.model.AuthCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthCredentialRepository extends JpaRepository<AuthCredential, UUID> {
    Optional<AuthCredential> findByEmail(String email);
    Optional<AuthCredential> findByMobile(String mobile);
    Optional<AuthCredential> findByUserId(UUID userId);
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);
}
