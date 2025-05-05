package com.reminderapp.reminder.service.dto;

import java.time.LocalDateTime;

public record ReminderDto(
        Long reminderId,
        Long userId,
        String title,
        String description,
        LocalDateTime remind
) {}
