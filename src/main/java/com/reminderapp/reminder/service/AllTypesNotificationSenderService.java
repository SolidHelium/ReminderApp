package com.reminderapp.reminder.service;

import com.reminderapp.reminder.entity.Reminder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class AllTypesNotificationSenderService {
    private final List<NotificationService> notificationServices;

    public void sendAllNotifications(Reminder reminder) {
        log.info("Starting sending all reminder notifications for reminder: {}", reminder);
        List<CompletableFuture<Void>> futures = notificationServices.stream()
                .filter(NotificationService::isEnabled)
                .map(service -> sendNotificationAsync(service, reminder))
                .toList();

        CompletableFuture<Void> allNotifications = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allNotifications.thenRun(() ->
                log.info("All notification services completed for reminder: {}", reminder.getReminderId())
                ).exceptionally(throwable -> {
                    log.error("some notification services failed for reminder {}", reminder.getReminderId(), throwable);
                    return null;
        });
    }

    private CompletableFuture<Void> sendNotificationAsync(NotificationService service, Reminder reminder) {
        return CompletableFuture.runAsync(() -> {
            try {
                service.sendNotification(reminder);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't send notification asynchronously: " + e);
            }
        });
    }
}
