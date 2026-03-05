package com.mpvaitheeswaran.ecommerce.auth_service.controller;

import com.mpvaitheeswaran.ecommerce.auth_service.dto.request.*;
import com.mpvaitheeswaran.ecommerce.auth_service.dto.response.MfaSetupResponse;
import com.mpvaitheeswaran.ecommerce.auth_service.dto.response.RegisterResponse;
import com.mpvaitheeswaran.ecommerce.auth_service.dto.response.TokenResponse;
import com.mpvaitheeswaran.ecommerce.auth_service.model.AuthCredential;
import com.mpvaitheeswaran.ecommerce.auth_service.repository.AuthCredentialRepository;
import com.mpvaitheeswaran.ecommerce.auth_service.service.AuthService;
import com.mpvaitheeswaran.ecommerce.auth_service.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;
    private final AuthCredentialRepository authCredentialRepository;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<TokenResponse> verifyOTP(@Valid @RequestBody VerifyOTPRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/login/otp")
    public ResponseEntity<TokenResponse> loginWithOtp(@Valid @RequestBody OtpLoginRequest request) {
        // Step 1: Send OTP to mobile (call separately if needed)
        // Step 2: Client calls this endpoint with otp received via SMS
        return ResponseEntity.ok(authService.loginWithOtp(request));
    }

    @PostMapping("/login/otp/send")
    public ResponseEntity<Void> sendLoginOtp(@RequestParam String mobile) {
        // Look up user, generate OTP, fire SMS via notification-service event
        Optional<AuthCredential> cred = authCredentialRepository.findByMobile(mobile); // fetch from repo
                otpService.generateAndStoreOtp(cred.get().getUserId());
        // publishEvent(new OtpGeneratedEvent(...))
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login/social")
    public ResponseEntity<TokenResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        // Here you would typically call a service to authenticate the user using social login and generate a JWT token if valid.
        // For demonstration purposes, we'll just return a dummy token response.
        return ResponseEntity.ok(new TokenResponse("dummy-jwt-token", "",190,"Bearer"));
    }

    // ── This is the key endpoint for token renewal ────────────────────────────
    // Client calls this when access token expires (401 received on any API).
    // Sends the stored refresh token → gets a fresh access token back.
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        // authentication.getPrincipal() = userId (set by JwtAuthenticationFilter)
        UUID userId = (UUID) authentication.getPrincipal();
        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        // Always return 200 — don't reveal if email exists
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mfa/setup")
    public ResponseEntity<MfaSetupResponse> setupMFA() {
        // Here you would typically call a service to handle the MFA setup logic, such as generating a secret key and providing a QR code for the user to scan with their authenticator app.
        // For demonstration purposes, we'll just return a dummy secret key.
        return ResponseEntity.ok(new MfaSetupResponse("dummy", URI.create("https://example.com/qrcode")));
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<TokenResponse> verifyMFA(@Valid @RequestBody MfaVerifyRequest request) {
        // Here you would typically call a service to verify the MFA code and generate a JWT token if valid.
        // For demonstration purposes, we'll just return a dummy token response.
        return ResponseEntity.ok(new TokenResponse("dummy-jwt-token", "", 190, "Bearer"));
    }

}
