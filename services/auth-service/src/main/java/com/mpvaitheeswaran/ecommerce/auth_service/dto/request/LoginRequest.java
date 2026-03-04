package com.mpvaitheeswaran.ecommerce.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Invalid email format")
        String email,

        @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be 10 digits")
        String mobile,

        @NotBlank(message = "Password is required")
        String password
) {}
