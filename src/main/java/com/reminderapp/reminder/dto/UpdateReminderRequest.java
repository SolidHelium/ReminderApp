package com.reminderapp.reminder.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateReminderRequest(
        Long reminderId,
        String title,
        String description,
        LocalDateTime remind
) {}
