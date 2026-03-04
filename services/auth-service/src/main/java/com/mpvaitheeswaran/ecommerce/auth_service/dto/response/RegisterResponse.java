package com.mpvaitheeswaran.ecommerce.auth_service.dto.response;

import java.util.UUID;

public record RegisterResponse(
        String message,
        UUID userId
) {}