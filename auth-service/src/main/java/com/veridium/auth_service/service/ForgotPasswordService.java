package com.veridium.auth_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veridium.auth_service.config.OtpGenerator;
import com.veridium.auth_service.config.RefreshTokenGenerator;
import com.veridium.auth_service.constants.Constants;
import com.veridium.auth_service.dto.ForgotPasswordDto;
import com.veridium.auth_service.dto.ForgotPasswordEmailEvent;
import com.veridium.auth_service.entity.*;
import com.veridium.auth_service.exception.tenant.TenantNotFoundException;
import com.veridium.auth_service.exception.user.UserNotFoundException;
import com.veridium.auth_service.repository.*;
import com.veridium.auth_service.utils.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final TenantRepository tenantRepository;
    private final OtpGenerator otpGenerator;
    private final PasswordResestRepository passwordResestRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final OutboxRepository outboxRepository;

    @Value("${app.rabbitmq.forgot-password-exchange}")
    private String exchange;

    @Value("${app.rabbitmq.forgot-password-routing-key}")
    private String routingKey;

    @Transactional
    public String forgotPassword(ForgotPasswordDto forgotPasswordDto) throws JsonProcessingException {
        String email = forgotPasswordDto.email();
        String tenantSlug = forgotPasswordDto.tenantSlug();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        Tenant tenant = tenantRepository.findBySlug(tenantSlug).orElseThrow(TenantNotFoundException::new);
        String otp = otpGenerator.generateOtp();
        String otpHash = Hash.hashify(otp);
        PasswordResetOtp passwordResetOtp = new PasswordResetOtp(user.getId(), otpHash, false);
        passwordResestRepository.save(passwordResetOtp);

        ForgotPasswordEmailEvent forgotPasswordEmailEvent = new ForgotPasswordEmailEvent(user.getEmail(), user.getFirst_name(), user.getLast_name(), otp,tenant.getName());
        ObjectMapper objectMapper = new ObjectMapper();


        OutboxEvent outboxEvent = new OutboxEvent("USER", user.getId(), Constants.FORGOT_PASSWORD,exchange,routingKey, objectMapper.writeValueAsString(forgotPasswordEmailEvent));
        outboxRepository.save(outboxEvent);

      //  rabbitTemplate.convertAndSend(exchange, routingKey, forgotPasswordEmailEvent);

        String refreshToken = refreshTokenGenerator.generate();
        String  refreshTokenHash = Hash.hashify(refreshToken);
        RefreshToken refreshTokenEntity = new RefreshToken(refreshTokenHash,user.getId(),forgotPasswordDto.device(),forgotPasswordDto.ip(),false);
        refreshTokenRepository.save(refreshTokenEntity);
        return refreshToken;
    }
}
