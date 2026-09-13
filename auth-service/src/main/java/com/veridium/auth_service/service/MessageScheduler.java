package com.veridium.auth_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veridium.auth_service.constants.Constants;
import com.veridium.auth_service.dto.UserSentRequestDto;
import com.veridium.auth_service.entity.OutboxEvent;
import com.veridium.auth_service.repository.OutboxRepository;
import com.veridium.auth_service.utils.OutboxStatus;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    public void processForgotPasswordEmail() {
            List<OutboxEvent> events = outboxRepository.findByStatusIn(targetStatuses);
            events.forEach((evt)-> {
                if(evt.getRetryCount() <= Constants.MAX_RETRY){
                    try{
                        Class<?> eventClass = Class.forName(evt.getEventType());
                        ObjectMapper objectMapper = new ObjectMapper();
                        Object payload =  objectMapper.readValue(
                                evt.getPayload(),
                                eventClass

                        );
                        rabbitTemplate.convertAndSend(evt.getExchange(),evt.getRoutingKey(),payload);
                        evt.markPublished();
                    }
                    catch (Exception e){
                        evt.markFailed(e.getMessage());
                    }
                }

            });
            outboxRepository.saveAll(events);
    }

}
