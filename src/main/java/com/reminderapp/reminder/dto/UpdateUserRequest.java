package com.reminderapp.reminder.dto;

import lombok.Builder;

@Builder
public record UpdateUserRequest(
        String name,
        String email,
        String telegram
) {}
