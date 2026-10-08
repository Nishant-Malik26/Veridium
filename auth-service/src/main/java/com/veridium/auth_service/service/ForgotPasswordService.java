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

import java.util.List;


@Service
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RabbitTemplate rabbitTemplate;
    private final TenantRepository tenantRepository;
    private final OtpGenerator otpGenerator;
    private final PasswordResetRepository passwordResetRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.rabbitmq.forgot-password-exchange}")
    private String exchange;

    @Value("${app.rabbitmq.forgot-password-routing-key}")
    private String routingKey;

    @Transactional
    public void forgotPassword(ForgotPasswordDto forgotPasswordDto)  {
        String email = forgotPasswordDto.email();
        String tenantSlug = forgotPasswordDto.tenantSlug();
        List<UserRole> userRoleList = userRoleRepository.findByUserEmailAndTenantSlug(email, tenantSlug);
        if(userRoleList.isEmpty()){
            throw new UserNotFoundException();
        }
        User user = userRoleList.getFirst().getUser();
        Tenant tenant = userRoleList.getFirst().getTenant();
        String otp = otpGenerator.generateOtp();
        String otpHash = Hash.hashify(otp);
        PasswordResetOtp passwordResetOtp = new PasswordResetOtp(user.getId(), otpHash, false);
        passwordResetRepository.save(passwordResetOtp);
        try {
            ForgotPasswordEmailEvent forgotPasswordEmailEvent = new ForgotPasswordEmailEvent(user.getEmail(), user.getFirst_name(), user.getLast_name(), otp,tenant.getName());
            OutboxEvent outboxEvent = new OutboxEvent(Constants.USER, user.getId().toString(), forgotPasswordEmailEvent.getClass().getName(),exchange,routingKey, objectMapper.writeValueAsString(forgotPasswordEmailEvent));
            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize email event", e);
        }
    }
}
