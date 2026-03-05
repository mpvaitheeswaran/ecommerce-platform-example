package com.mpvaitheeswaran.ecommerce.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS) // Returns 429 Too Many Requests
public class OtpMaxAttemptsException extends RuntimeException {
    public OtpMaxAttemptsException(String message) {
        super(message);
    }
}