package com.mpvaitheeswaran.ecommerce.auth_service.controller;

import com.mpvaitheeswaran.ecommerce.auth_service.dto.request.*;
import com.mpvaitheeswaran.ecommerce.auth_service.dto.response.MfaSetupResponse;
import com.mpvaitheeswaran.ecommerce.auth_service.dto.response.RegisterResponse;
import com.mpvaitheeswaran.ecommerce.auth_service.dto.response.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Here you would typically call a service to handle the registration logic,
        // such as saving the user to the database and generating a user ID.
        // For demonstration purposes, we'll just return a success message with a dummy user ID.
        return ResponseEntity.ok(new RegisterResponse("User registered successfully", java.util.UUID.randomUUID()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<TokenResponse> verifyOTP(@Valid @RequestBody VerifyOTPRequest request) {
        // Here you would typically call a service to verify the OTP and generate a JWT token if valid.
        // For demonstration purposes, we'll just return a dummy token response.

        return ResponseEntity.ok(new TokenResponse("dummy-jwt-token", "",190,"Bearer"));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        // Here you would typically call a service to authenticate the user and generate a JWT token if valid.
        // For demonstration purposes, we'll just return a dummy token response.
        return ResponseEntity.ok(new TokenResponse("dummy-jwt-token", "",190,"Bearer"));
    }

    @PostMapping("/login/otp")
    public ResponseEntity<TokenResponse> loginWithOtp(@Valid @RequestBody OtpLoginRequest request) {
        // Here you would typically call a service to authenticate the user using OTP and generate a JWT token if valid.
        // For demonstration purposes, we'll just return a dummy token response.

        return ResponseEntity.ok(new TokenResponse("dummy-jwt-token", "",190,"Bearer"));
    }

    @PostMapping("/login/social")
    public ResponseEntity<TokenResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        // Here you would typically call a service to authenticate the user using social login and generate a JWT token if valid.
        // For demonstration purposes, we'll just return a dummy token response.
        return ResponseEntity.ok(new TokenResponse("dummy-jwt-token", "",190,"Bearer"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        // Here you would typically call a service to validate the refresh token and generate a new JWT token if valid.
        // For demonstration purposes, we'll just return a dummy token response.
        return ResponseEntity.ok(new TokenResponse("new-dummy-jwt-token", "new-dummy-refresh-token", 190, "Bearer"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Here you would typically call a service to invalidate the refresh token and perform any necessary cleanup.
        // For demonstration purposes, we'll just return a 204 No Content response.
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        // Here you would typically call a service to handle the forgot password logic, such as sending a password reset email or SMS.
        // For demonstration purposes, we'll just return a 204 No Content response.
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        // Here you would typically call a service to handle the reset password logic, such as validating the reset token and updating the user's password in the database.
        // For demonstration purposes, we'll just return a 204 No Content response.
        return ResponseEntity.noContent().build();
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
