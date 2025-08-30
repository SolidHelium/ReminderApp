package com.reminderapp.reminder.dto;

import lombok.Builder;

@Builder
public record LoginRequest(
        String email,
        String password
) {}
