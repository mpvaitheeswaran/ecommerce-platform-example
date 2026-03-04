package com.mpvaitheeswaran.ecommerce.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MfaVerifyRequest(
        @NotBlank(message = "TOTP code is required")
        @Pattern(regexp = "^\\d{6}$", message = "TOTP must be exactly 6 digits")
        String totp
) {}