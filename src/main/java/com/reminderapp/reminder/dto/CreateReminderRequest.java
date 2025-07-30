package com.reminderapp.reminder.dto;


import java.time.LocalDateTime;

public record CreateReminderRequest(
        String title,
        String description,
        LocalDateTime remind
) {}
