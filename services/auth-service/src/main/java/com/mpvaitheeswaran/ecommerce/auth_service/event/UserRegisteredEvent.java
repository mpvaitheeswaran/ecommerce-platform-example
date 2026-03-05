package com.mpvaitheeswaran.ecommerce.auth_service.event;

import java.util.UUID;

public record UserRegisteredEvent(UUID userId, String name, String email, String mobile, String role) {}