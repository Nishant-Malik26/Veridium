package com.veridium.auth_service.entity;

import com.veridium.auth_service.utils.OutboxStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
//import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "outbox_event")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private String exchange;
    @Column(name = "routing_key")
    private String routingKey;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at", insertable = false )
    private LocalDateTime publishedAt;

    @PrePersist
    public void prePersist() {
        status = OutboxStatus.PENDING;
        retryCount = 0;
    }

    public void markPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markFailed(String error) {
        this.status = OutboxStatus.FAILED;
        this.retryCount++;
        this.lastError = error;
        this.nextRetryAt = LocalDateTime.now().plusMinutes(5);
    }

    public OutboxEvent(
            String aggregateType,
            String aggregateId,
            String eventType,
            String exchangeName,
            String routingKey,
            String payload
    ) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.exchange = exchangeName;
        this.routingKey = routingKey;
        this.payload = payload;
    }

}
