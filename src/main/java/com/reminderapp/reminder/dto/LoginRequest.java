package com.reminderapp.reminder.dto;

public record LoginRequest(
        String email,
        String password
) {}
