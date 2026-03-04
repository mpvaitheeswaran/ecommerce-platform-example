package com.mpvaitheeswaran.ecommerce.auth_service.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType
) {}