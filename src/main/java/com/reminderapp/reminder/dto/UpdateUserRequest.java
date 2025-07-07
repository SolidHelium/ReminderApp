package com.reminderapp.reminder.dto;

public record UpdateUserRequest(
        String name,
        String email,
        String telegram
) {}
