package com.mpvaitheeswaran.ecommerce.auth_service.dto.request;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(
        @NotNull(message = "refreshToken is required")
        String refreshToken
) {
}
