package com.reminderapp.reminder.service;

import com.reminderapp.reminder.entity.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

//TODO: Узнать, почему так медленно работает

@Service
public class EmailNotificationService implements NotificationService {
    private static Logger log = LoggerFactory.getLogger(EmailNotificationService.class);
    private final JavaMailSender mailSender;
    private String emailSenderAddress;

    @Autowired
    public EmailNotificationService(
            JavaMailSender mailSender,
            @Value("${app.notification.email.from}")
            String emailSenderAddress
    ) {
        this.mailSender = mailSender;
        this.emailSenderAddress = emailSenderAddress;
    }

    @Override
    public void sendNotification(Reminder reminder) {
        if (!isEnabled()) {
            log.info("Email notifications are disabled for user {}", reminder.getUser().getEmail());
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailSenderAddress);
            message.setTo(reminder.getUser().getEmail());
            message.setSubject(reminder.getTitle());
            message.setText(reminder.getDescription());
            mailSender.send(message);

            log.info("Email notification sent successfully. User - {}", reminder.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}",reminder.getUser().getEmail(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //TODO: Notification enabling/disabling system
    @Override
    public boolean isEnabled() {
        return true;
    }
}
