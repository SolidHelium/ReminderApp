package com.reminderapp.reminder.dto;

import lombok.Builder;

@Builder
public record CreateUserRequest(
        String name,
        String email,
        String password,
        String telegram
) {}
