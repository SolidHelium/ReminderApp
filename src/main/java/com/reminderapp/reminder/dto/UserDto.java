package com.reminderapp.reminder.dto;

import com.reminderapp.reminder.entity.Reminder;
import com.reminderapp.reminder.specification.UserRole;
import lombok.Builder;

import java.util.List;

@Builder
public record UserDto(
        Long userId,
        String name,
        UserRole role,
        String email,
        String telegram
) {}
