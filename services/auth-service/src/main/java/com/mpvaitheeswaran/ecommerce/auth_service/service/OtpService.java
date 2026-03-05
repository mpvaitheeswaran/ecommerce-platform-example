package com.mpvaitheeswaran.ecommerce.auth_service.service;

import com.mpvaitheeswaran.ecommerce.auth_service.exception.OtpExpiredException;
import com.mpvaitheeswaran.ecommerce.auth_service.exception.OtpMaxAttemptsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate redisTemplate;

    private static final long OTP_TTL_MINUTES   = 10;
    private static final int  MAX_OTP_ATTEMPTS  = 3;
    private static final String OTP_PREFIX      = "otp:";
    private static final String OTP_ATTEMPTS    = "otp:attempts:";

    // Generate and store OTP for userId
    public String generateAndStoreOtp(UUID userId) {
        String otp = String.format("%06d",
                new java.security.SecureRandom().nextInt(999999));

        String key = OTP_PREFIX + userId.toString();
        redisTemplate.opsForValue().set(key, otp,
                Duration.ofMinutes(OTP_TTL_MINUTES));

        // Reset attempt counter
        redisTemplate.delete(OTP_ATTEMPTS + userId.toString());

        return otp;
    }

    // Verify OTP — returns true if valid, false if wrong
    // Throws exception if expired or max attempts exceeded
    public boolean verifyOtp(UUID userId, String inputOtp) {
        String attemptsKey = OTP_ATTEMPTS + userId.toString();
        String otpKey      = OTP_PREFIX   + userId.toString();

        String stored = redisTemplate.opsForValue().get(otpKey);
        if (stored == null) {
            throw new OtpExpiredException("OTP expired or not found");
        }

        // Increment attempt counter
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        if (attempts == 1) {
            redisTemplate.expire(attemptsKey, Duration.ofMinutes(OTP_TTL_MINUTES));
        }

        if (attempts > MAX_OTP_ATTEMPTS) {
            redisTemplate.delete(otpKey);
            throw new OtpMaxAttemptsException("Max OTP attempts exceeded");
        }

        if (!stored.equals(inputOtp)) {
            return false;
        }

        // Valid — clean up
        redisTemplate.delete(otpKey);
        redisTemplate.delete(attemptsKey);
        return true;
    }
}