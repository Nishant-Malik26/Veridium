package com.veridium.auth_service.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.veridium.auth_service.constants.Constants;
import com.veridium.auth_service.dto.*;
import com.veridium.auth_service.entity.*;
import com.veridium.auth_service.exception.invitation.InvitationNotFound;
import com.veridium.auth_service.exception.role.RoleNotFoundException;
import com.veridium.auth_service.repository.*;
import com.veridium.auth_service.utils.InvitationStatus;
import com.veridium.auth_service.utils.UuidEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final UserService userService;
    private final OutboxRepository outboxRepository;
    private final RoleService roleService;
    private final RoleRepository roleRepository;


    @Value("${app.rabbitmq.invitation-exchange}")
    private String exchange;

    @Value("${app.rabbitmq.invitation-routing-key}")
    private String routingKey;

    @Transactional
    public boolean sendInvitation(UserInvitationSendRequest userInvitationSendRequest) throws JsonProcessingException {
        List<Role> rolesList = userInvitationSendRequest.roles().stream().map(dto -> roleRepository.findById(dto).orElseThrow(RoleNotFoundException::new)).toList();

        UUID uuid = UUID.randomUUID();
        String token = UuidEncoder.encode(uuid);
        UserInvitation invitation = new UserInvitation(token, userInvitationSendRequest.tenantId(), userInvitationSendRequest.tenantName(), InvitationStatus.SENT, rolesList);
        UserInvitation savedInvitation = invitationRepository.save(invitation);
        UserSentRequestDto tokenDto = new UserSentRequestDto(token,  savedInvitation.getTenantId(), savedInvitation.getTenantName(), userInvitationSendRequest.receiverEmail());
        ObjectMapper objectMapper = new ObjectMapper();

        OutboxEvent outboxEvent = new OutboxEvent(Constants.INVITATION, savedInvitation.getId().toString(),tokenDto.getClass().getName(), exchange, routingKey, objectMapper.writeValueAsString(tokenDto));
        outboxRepository.save(outboxEvent);
        return true;
    }

    @Transactional
    public boolean acceptInvitation(UserAcceptionAndCreationDto request) {
        UserInvitationAcceptRequest acceptRequest =
                request.userInvitationAcceptRequest();

        // 1. Find invitation
        UserInvitation userInvitation =
                invitationRepository
                        .findByTokenAndTenantId(
                                acceptRequest.token(),
                                acceptRequest.tenantId()
                        )
                        .orElseThrow(InvitationNotFound::new);


        // 2. Check if invitation is already accepted
        if (userInvitation.getStatus() == InvitationStatus.ACCEPTED) {
            throw new InvitationNotFound();
        }


        // 3. Calculate expiration
        OffsetDateTime currentTime =
                OffsetDateTime.now(ZoneOffset.UTC);

        OffsetDateTime expiryTime =
                userInvitation
                        .getOrderTimestamp()
                        .plusMinutes(
                                Constants.INVITATION_EXPIRATION_IN_MINUTES
                        );


        // 4. Check expiration
        if (currentTime.isAfter(expiryTime)) {
            throw new InvitationNotFound();
        }


        // 5. Create user
        UserDto savedUser =
                userService.createUser(
                        request.userCreationRequest()
                );


        // 6. Get roles directly from invitation
        List<Role> roles =
                new ArrayList<>(
                        userInvitation.getRoles()
                );


        // 7. Assign roles
        roleService.assignRoles(
                roles,
                savedUser.id(),
                acceptRequest.tenantId()
        );


        // 8. Mark invitation as accepted
        userInvitation.accept();

        invitationRepository.save(userInvitation);


        return true;
    }
}
