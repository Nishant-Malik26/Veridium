package com.veridium.auth_service.repository;

import com.veridium.auth_service.entity.OutboxEvent;
import com.veridium.auth_service.utils.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatusIn(List<OutboxStatus> statuses);
}
