package com.mpvaitheeswaran.ecommerce.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name is required")
        String name,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be 10 digits")
        String mobile,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        String role // Defaulted to CUSTOMER based on your JSON
) {}
