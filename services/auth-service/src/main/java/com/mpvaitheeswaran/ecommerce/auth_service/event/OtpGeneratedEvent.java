package com.mpvaitheeswaran.ecommerce.auth_service.event;

import java.util.UUID;

public record OtpGeneratedEvent(UUID userId, String email, String mobile, String otp) {}
