package com.veridium.auth_service.service;

import com.veridium.auth_service.dto.ForgotPasswordEmailEvent;
import com.veridium.auth_service.dto.UserSentRequestDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
public class NotificationSender {

    @RabbitListener(queues = "${app.rabbitmq.forgot-password-queue}")
    public void sendForgotPassword(ForgotPasswordEmailEvent event) {
            //TODO: AWS Ses for email sending until then create a file and write OTP there
        try {

            String logContent = String.format("Email: %s | OTP: %s | Timestamp: %s%n",
                                              event.email(), event.otp(), java.time.LocalDateTime.now());

            Path filePath = Path.of("forgot_password_otps.txt");
            Files.writeString(filePath, logContent,
                              StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println(" sending forgot password email event"+ logContent );
            System.out.println(" sending forgot password email event"+ filePath.toAbsolutePath() );


        } catch (IOException e) {
            System.out.println("Error sending forgot password email event"+ e.getMessage() );
        }
    }

    @RabbitListener(queues = "${app.rabbitmq.invitation-queue}")
    public void sendInvitation(UserSentRequestDto event) {
        try {

            String logContent = String.format("Email: %s | Timestamp: %s%n",
                                              event.token(), java.time.LocalDateTime.now());

            Path filePath = Path.of("sent-invitation.txt");
            Files.writeString(filePath, logContent,
                              StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println(" sending forgot password email event"+ logContent );
            System.out.println(" sending forgot password email event"+ filePath.toAbsolutePath() );


        } catch (IOException e) {
            System.out.println("Error sending forgot password email event"+ e.getMessage() );
        }
    }
}
