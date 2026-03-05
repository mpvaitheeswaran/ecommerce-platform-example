package com.mpvaitheeswaran.ecommerce.auth_service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class AuthEventListener {

    // Runs only after the DB transaction is successful
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async // Runs in a background thread
    public void handleOtpGenerated(OtpGeneratedEvent event) {
        log.info("Sending OTP {} to {}", event.otp(), event.email());
        // Call notificationService.sendSms(...) or sendEmail(...) here
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Creating profile for user: {}", event.userId());
        // Call userService.createProfile(...) here
    }
}