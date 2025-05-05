package com.reminderapp.reminder.service.dto;


import java.time.LocalDateTime;

public record UpdateReminderRequest(
        Long reminderId,
        String title,
        String description,
        LocalDateTime remind
) {}
