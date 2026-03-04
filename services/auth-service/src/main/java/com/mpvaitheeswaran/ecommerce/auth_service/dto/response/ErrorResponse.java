package com.mpvaitheeswaran.ecommerce.auth_service.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> fieldErrors
) {
    public record FieldError(
            String field,
            String message
    ) {}
}