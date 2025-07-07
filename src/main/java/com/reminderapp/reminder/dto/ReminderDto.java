package com.reminderapp.reminder.dto;

import java.time.LocalDateTime;

public record ReminderDto(
        Long reminderId,
        Long userId,
        String title,
        String description,
        LocalDateTime remind
) {}
//TODO: Keep only one reminder DTO

