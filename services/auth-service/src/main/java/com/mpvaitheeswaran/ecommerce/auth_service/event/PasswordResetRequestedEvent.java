package com.mpvaitheeswaran.ecommerce.auth_service.event;

public record PasswordResetRequestedEvent(String email, String token) {}
