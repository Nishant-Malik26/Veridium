package com.veridium.auth_service.service;

import com.veridium.auth_service.constants.Constants;
import com.veridium.auth_service.entity.OutboxEvent;
import com.veridium.auth_service.repository.OutboxRepository;
import com.veridium.auth_service.utils.OutboxStatus;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class MessageScheduler {
    private final RabbitTemplate rabbitTemplate;
    private final OutboxRepository outboxRepository;

    List<OutboxStatus> targetStatuses = List.of(OutboxStatus.PENDING,
                                          OutboxStatus.FAILED);

    public MessageScheduler(RabbitTemplate rabbitTemplate, OutboxRepository outboxRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void executeTask() {
        try{
            List<OutboxEvent> events = outboxRepository.findByStatusIn(targetStatuses);
            events.forEach((evt)-> {
                if(evt.getRetryCount() <= Constants.MAX_RETRY){
                    try{
                        rabbitTemplate.convertAndSend(evt.getExchange(),evt.getRoutingKey(),evt.getPayload());
                        evt.markPublished();
                    }
                    catch (Exception e){
                        evt.markFailed(e.getMessage());
                    }
                }

            });
            outboxRepository.saveAll(events);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
