package com.reminderapp.reminder.service;

import com.reminderapp.reminder.entity.Reminder;

public interface NotificationService {
    void sendNotification(Reminder reminder);
    boolean isEnabled();
}
