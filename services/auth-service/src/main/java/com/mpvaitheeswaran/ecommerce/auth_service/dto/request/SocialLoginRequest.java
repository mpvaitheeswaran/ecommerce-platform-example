package com.mpvaitheeswaran.ecommerce.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialLoginRequest(
        @NotNull(message = "Provider is required")
        AuthProvider provider,

        @NotBlank(message = "ID Token is required")
        String idToken
) {
    public enum AuthProvider {
        GOOGLE, FACEBOOK, APPLE
    }
}