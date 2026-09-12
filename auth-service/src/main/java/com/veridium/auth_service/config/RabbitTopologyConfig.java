package com.veridium.auth_service.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfig {

    @Value("${app.rabbitmq.invitation-exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.invitation-queue}")
    private String queueName;

    @Value("${app.rabbitmq.invitation-routing-key}")
    private String routingKey;

    @Value("${app.rabbitmq.forgot-password-exchange}")
    private String exchangeNameForgotPassword;

    @Value("${app.rabbitmq.forgot-password-queue}")
    private String queueNameForgotPassword;

    @Value("${app.rabbitmq.forgot-password-routing-key}")
    private String routingKeyForgotPass;




    @Bean(name = "invitationExchange")
    public DirectExchange invitationExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean(name = "invitationQueue")
    public Queue invitationQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding invitationBinding(@Qualifier("invitationQueue") Queue invitationQueue,@Qualifier("invitationExchange") DirectExchange invitationExchange) {
        return BindingBuilder.bind(invitationQueue).to(invitationExchange).with(routingKey);
    }

    @Bean("forgotPasswordExchange")
    public DirectExchange forgotPasswordExchange() {
        return new DirectExchange(exchangeNameForgotPassword);
    }

    @Bean(name = "forgotPasswordQueue")
    public Queue forgotPasswordQueue() {
        return QueueBuilder.durable(queueNameForgotPassword).build();
    }

    @Bean
    public Binding forgotPasswordBinding(@Qualifier("forgotPasswordQueue") Queue queueNameForgotPassword, @Qualifier("forgotPasswordExchange") DirectExchange exchangeNameForgotPassword) {
        return BindingBuilder.bind(queueNameForgotPassword).to(exchangeNameForgotPassword).with(routingKeyForgotPass);
    }
}

