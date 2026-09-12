package com.veridium.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateOtp() {
        int otp = RANDOM.nextInt(1_000_000);
        return String.format("%06d", otp);
    }
}
