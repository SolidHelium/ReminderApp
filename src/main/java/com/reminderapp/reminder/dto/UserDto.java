package com.reminderapp.reminder.dto;

import com.reminderapp.reminder.specification.UserRole;
import lombok.Builder;

@Builder
public record UserDto(
        Long userId,
        String name,
        UserRole role,
        String email,
        String telegram
) {}
