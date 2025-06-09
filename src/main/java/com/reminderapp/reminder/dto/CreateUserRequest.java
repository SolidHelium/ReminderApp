package com.reminderapp.reminder.dto;

public record CreateUserRequest(
        String name,
        String email,
        String password,
        String telegram
) {}
