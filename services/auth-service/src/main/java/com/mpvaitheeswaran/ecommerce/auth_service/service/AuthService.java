package com.mpvaitheeswaran.ecommerce.auth_service.service;

import com.mpvaitheeswaran.ecommerce.auth_service.dto.request.*;
import com.mpvaitheeswaran.ecommerce.auth_service.dto.response.RegisterResponse;
import com.mpvaitheeswaran.ecommerce.auth_service.dto.response.TokenResponse;
import com.mpvaitheeswaran.ecommerce.auth_service.event.OtpGeneratedEvent;
import com.mpvaitheeswaran.ecommerce.auth_service.event.PasswordResetRequestedEvent;
import com.mpvaitheeswaran.ecommerce.auth_service.event.UserRegisteredEvent;
import com.mpvaitheeswaran.ecommerce.auth_service.exception.*;
import com.mpvaitheeswaran.ecommerce.auth_service.model.AuthCredential;
import com.mpvaitheeswaran.ecommerce.auth_service.model.PasswordResetToken;
import com.mpvaitheeswaran.ecommerce.auth_service.model.RefreshToken;
import com.mpvaitheeswaran.ecommerce.auth_service.repository.AuthCredentialRepository;
import com.mpvaitheeswaran.ecommerce.auth_service.repository.PasswordResetTokenRepository;
import com.mpvaitheeswaran.ecommerce.auth_service.repository.RefreshTokenRepository;
import com.mpvaitheeswaran.ecommerce.auth_service.security.util.JwtUtil;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthCredentialRepository credentialRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final PasswordResetTokenRepository resetTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService               otpService;
    private final ApplicationEventPublisher eventPublisher;    // for Kafka / email events
    private final StringRedisTemplate redis;

    private static final int  MAX_FAILED_ATTEMPTS  = 5;
    private static final long LOCK_DURATION_MINUTES = 15;
    private static final String LOGIN_FAIL_KEY      = "login:fail:";

    // ─────────────────────────────────────────────────────────────────────────
    // REGISTER
    // ─────────────────────────────────────────────────────────────────────────
    public RegisterResponse register(RegisterRequest req) {
        // 1. Validate at least email or mobile is provided
        if (req.email() == null && req.mobile() == null) {
            throw new ValidationException("Email or mobile is required");
        }
        // 2. Check for duplicates
        if (req.email() != null && credentialRepo.existsByEmail(req.email())) {
            throw new DuplicateResourceException("Email already registered");
        }
        if (req.mobile() != null && credentialRepo.existsByMobile(req.mobile())) {
            throw new DuplicateResourceException("Mobile already registered");
        }

        // 3. Create userId (user-service will create the profile on user.registered event)
        UUID userId = UUID.randomUUID();

        // 4. Save credentials with hashed password
        AuthCredential credential = AuthCredential.builder()
                .userId(userId)
                .email(req.email())
                .mobile(req.mobile())
                .passwordHash(passwordEncoder.encode(req.password()))
                .build();
        credentialRepo.save(credential);

        // 5. Generate OTP and send it (email or SMS)
        String otp = otpService.generateAndStoreOtp(userId);
        // Publish event → notification-service handles actual email/SMS delivery
        eventPublisher.publishEvent(new OtpGeneratedEvent(userId, req.email(),
                req.mobile(), otp));

        // 6. Publish user.registered event → user-service creates profile
        eventPublisher.publishEvent(new UserRegisteredEvent(userId,
                req.name(), req.email(), req.mobile(), req.role()));

        log.info("User registered: userId={}", userId);
        return new RegisterResponse("OTP sent for verification", userId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VERIFY OTP  →  returns full token pair
    // ─────────────────────────────────────────────────────────────────────────
    public TokenResponse verifyOtp(VerifyOTPRequest req) {
        AuthCredential credential = credentialRepo.findByUserId(req.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify OTP from Redis
        boolean valid = otpService.verifyOtp(req.userId(), req.otp());
        if (!valid) {
            throw new InvalidOtpException("Invalid OTP");
        }

        // Mark as verified
        if (credential.getEmail() != null) credential.setEmailVerified(true);
        if (credential.getMobile() != null) credential.setMobileVerified(true);
        credentialRepo.save(credential);

        return buildTokenPair(credential);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOGIN with email + password
    // ─────────────────────────────────────────────────────────────────────────
    public TokenResponse login(LoginRequest req) {
        // 1. Find credential by email or mobile
        AuthCredential credential = findByEmailOrMobile(req.email(), req.mobile());

        // 2. Check account status
        checkAccountStatus(credential);

        // 3. Verify password
        if (!passwordEncoder.matches(req.password(), credential.getPasswordHash())) {
            handleFailedAttempt(credential);
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // 4. Reset failed attempts on success
        credential.setFailedAttempts(0);
        credential.setLastLoginAt(Instant.now());
        credentialRepo.save(credential);

        redis.delete(LOGIN_FAIL_KEY + credential.getUserId());

        return buildTokenPair(credential);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOGIN with mobile OTP
    // ─────────────────────────────────────────────────────────────────────────
    public TokenResponse loginWithOtp(OtpLoginRequest req) {
        AuthCredential credential = credentialRepo.findByMobile(req.mobile())
                .orElseThrow(() -> new ResourceNotFoundException("Mobile not registered"));

        checkAccountStatus(credential);

        boolean valid = otpService.verifyOtp(credential.getUserId(), req.otp());
        if (!valid) {
            throw new InvalidOtpException("Invalid OTP");
        }

        credential.setLastLoginAt(Instant.now());
        credentialRepo.save(credential);

        return buildTokenPair(credential);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REFRESH ACCESS TOKEN  ← this is what you asked specifically about
    //
    // Flow:
    //  1. Hash the incoming raw refresh token
    //  2. Look it up in DB
    //  3. Check not revoked, not expired
    //  4. Issue NEW access token (same refresh token re-used until it expires)
    //     OR rotate refresh token (more secure — new refresh token issued)
    // ─────────────────────────────────────────────────────────────────────────
    public TokenResponse refreshAccessToken(RefreshTokenRequest req) {
        // 1. Hash the raw token that was sent by client
        String tokenHash = jwtUtil.hashRefreshToken(req.refreshToken());

        // 2. Look up in DB
        RefreshToken stored = refreshTokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        // 3. Validate
        if (stored.isRevoked()) {
            // Possible token theft — revoke ALL tokens for this user
            refreshTokenRepo.deleteAllByCredential(stored.getCredential());
            throw new InvalidTokenException("Refresh token revoked. All sessions invalidated.");
        }
        if (stored.isExpired()) {
            throw new InvalidTokenException("Refresh token expired. Please login again.");
        }

        AuthCredential credential = stored.getCredential();
        checkAccountStatus(credential);

        // 4. Rotate refresh token — revoke old, issue new (recommended security practice)
        stored.setRevokedAt(Instant.now());
        refreshTokenRepo.save(stored);

        // 5. Issue new access token + new refresh token
        return buildTokenPair(credential);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOGOUT — revoke all refresh tokens for the user
    // ─────────────────────────────────────────────────────────────────────────
    public void logout(UUID userId) {
        AuthCredential credential = credentialRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        refreshTokenRepo.deleteAllByCredential(credential);
        log.info("User logged out: userId={}", userId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FORGOT PASSWORD
    // ─────────────────────────────────────────────────────────────────────────
    public void forgotPassword(ForgotPasswordRequest req) {
        // Don't reveal whether email exists — always return 200
        credentialRepo.findByEmail(req.email()).ifPresent(credential -> {
            // Invalidate previous reset tokens
            resetTokenRepo.deleteAllByCredential(credential);

            // Generate secure token
            String rawToken = UUID.randomUUID().toString();
            String tokenHash = jwtUtil.hashRefreshToken(rawToken); // reuse SHA-256 util

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .credential(credential)
                    .tokenHash(tokenHash)
                    .expiresAt(Instant.now().plusSeconds(3600)) // 1 hour
                    .build();
            resetTokenRepo.save(resetToken);

            // Publish event → notification-service sends email
            eventPublisher.publishEvent(new PasswordResetRequestedEvent(
                    credential.getEmail(), rawToken));
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RESET PASSWORD
    // ─────────────────────────────────────────────────────────────────────────
    public void resetPassword(ResetPasswordRequest req) {
        String tokenHash = jwtUtil.hashRefreshToken(req.token());

        PasswordResetToken resetToken = resetTokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token"));

        if (resetToken.isExpired()) {
            throw new InvalidTokenException("Password reset link has expired");
        }
        if (resetToken.isUsed()) {
            throw new InvalidTokenException("Password reset link already used");
        }

        // Update password
        AuthCredential credential = resetToken.getCredential();
        credential.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        credential.setFailedAttempts(0);
        credential.setLocked(false);
        credential.setLockedUntil(null);
        credentialRepo.save(credential);

        // Mark token as used
        resetToken.setUsedAt(Instant.now());
        resetTokenRepo.save(resetToken);

        // Invalidate all active sessions for security
        refreshTokenRepo.deleteAllByCredential(credential);

        log.info("Password reset for userId={}", credential.getUserId());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    // Builds BOTH access token and refresh token, persists refresh token to DB
    private TokenResponse buildTokenPair(AuthCredential credential) {
        // You need the user's role — fetch from user-service via internal API
        // OR denormalize role into auth_credentials table for performance
        // Here we assume role is stored in credentials for auth-service independence
        String role = "CUSTOMER"; // fetch from user-service or stored field

        // 1. Generate access token (JWT, short-lived, stateless)
        String accessToken = jwtUtil.generateAccessToken(
                credential.getUserId(),
                credential.getEmail(),
                role);

        // 2. Generate refresh token (opaque random string, long-lived, stored in DB)
        String rawRefreshToken = jwtUtil.generateRefreshToken();
        String tokenHash       = jwtUtil.hashRefreshToken(rawRefreshToken);

        // 3. Persist refresh token (DB is source of truth for revocation)
        RefreshToken refreshToken = RefreshToken.builder()
                .credential(credential)
                .tokenHash(tokenHash)
                .expiresAt(jwtUtil.refreshTokenExpiry())
                .build();
        refreshTokenRepo.save(refreshToken);

        return new TokenResponse(
                accessToken,
                rawRefreshToken,                              // raw token sent to client
                jwtUtil.getAccessTokenExpirySeconds(),
                "Bearer");
    }

    private AuthCredential findByEmailOrMobile(String email, String mobile) {
        if (email != null) {
            return credentialRepo.findByEmail(email)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        }
        if (mobile != null) {
            return credentialRepo.findByMobile(mobile)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        }
        throw new ValidationException("Email or mobile required");
    }

    private void checkAccountStatus(AuthCredential credential) {
        if (!credential.isActive()) {
            throw new AccountDeactivatedException("Account is deactivated");
        }
        if (credential.isLocked()) {
            if (credential.getLockedUntil() != null
                    && Instant.now().isAfter(credential.getLockedUntil())) {
                // Auto-unlock after lock duration
                credential.setLocked(false);
                credential.setFailedAttempts(0);
                credentialRepo.save(credential);
            } else {
                throw new AccountLockedException("Account locked. Try again later.");
            }
        }
    }

    private void handleFailedAttempt(AuthCredential credential) {
        int attempts = credential.getFailedAttempts() + 1;
        credential.setFailedAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            credential.setLocked(true);
            credential.setLockedUntil(Instant.now()
                    .plusSeconds(LOCK_DURATION_MINUTES * 60));
            log.warn("Account locked due to failed attempts: userId={}",
                    credential.getUserId());
        }
        credentialRepo.save(credential);
    }
}