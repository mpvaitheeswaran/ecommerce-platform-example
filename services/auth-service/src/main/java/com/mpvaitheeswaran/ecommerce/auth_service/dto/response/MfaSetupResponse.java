package com.mpvaitheeswaran.ecommerce.auth_service.dto.response;

import jakarta.validation.constraints.NotBlank;
import java.net.URI;

public record MfaSetupResponse(
        @NotBlank
        String secret,

        URI qrCodeUri
) {}