package com.veridium.auth_service.service;


import com.veridium.auth_service.dto.UserAcceptionAndCreationDto;
import com.veridium.auth_service.dto.UserInvitationSendRequest;
import com.veridium.auth_service.entity.UserInvitation;
import com.veridium.auth_service.exception.invitation.InvitationNotFound;
import com.veridium.auth_service.repository.InvitationRepository;
import com.veridium.auth_service.utils.InvitationStatus;
import com.veridium.auth_service.utils.UuidEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final RabbitTemplate rabbitTemplate;
    private final UserService userService;

    @Value("${app.rabbitmq.invitation-exchange}")
    private String exchange;

    @Value("${app.rabbitmq.invitation-routing-key}")
    private String routingKey;

    @Transactional
    public boolean sendInvitation(UserInvitationSendRequest userInvitationSendRequest) {
        UUID uuid = UUID.randomUUID();
        String token = UuidEncoder.encode(uuid);
        UserInvitation invitation = new UserInvitation(token, userInvitationSendRequest.tenantId(), userInvitationSendRequest.tenantName(), InvitationStatus.SENT);
        invitationRepository.save(invitation);
        rabbitTemplate.convertAndSend(exchange, routingKey, token);
        return true;
    }

    public boolean acceptInvitation(UserAcceptionAndCreationDto userInvitationSendRequest) {
        String token = userInvitationSendRequest.userInvitationAcceptRequest().token();
        UserInvitation userInvitation = invitationRepository.findByTokenAndTenantId(token, userInvitationSendRequest.userInvitationAcceptRequest().tenantId()).orElseThrow(InvitationNotFound::new);
        OffsetDateTime currentTime = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expiryTime = userInvitation.getOrderTimestamp().withOffsetSameInstant(ZoneOffset.UTC);
        Duration duration = Duration.between(currentTime, expiryTime);
        if(duration.isNegative()) {
            throw new InvitationNotFound();
        }
        userService.createUser(userInvitationSendRequest.userCreationRequest());
        return true;
    }
}
