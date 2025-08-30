package com.reminderapp.reminder.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateReminderRequest(
        String title,
        String description,
        LocalDateTime remind
) {}
