package com.veridium.auth_service.entity;

import com.veridium.auth_service.utils.InvitationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_invitation")
@NoArgsConstructor
@Getter
public class UserInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private UUID tenantId;
    private String tenantName;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_invitation_roles",
            joinColumns = @JoinColumn(name = "invitation_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    private InvitationStatus status;
    @Column(name = "order_timestamp", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime orderTimestamp;

    public UserInvitation(String token, UUID tenantId, String tenantName, InvitationStatus status, List<Role> roles ) {
        this.token = token;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.status = status;
        this.roles = roles != null
                ? new ArrayList<>(roles)
                : new ArrayList<>();
    }

    @PrePersist
    protected void onCreate() {
        this.orderTimestamp = OffsetDateTime.now();
    }

    public void accept() {
        this.status = InvitationStatus.ACCEPTED;
    }
}
