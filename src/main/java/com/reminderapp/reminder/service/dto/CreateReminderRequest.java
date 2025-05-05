package com.reminderapp.reminder.service.dto;


import java.time.LocalDateTime;

public record CreateReminderRequest(
        Long userId,
        String title,
        String description,
        LocalDateTime remind
) {}
