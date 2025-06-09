package com.reminderapp.reminder.dto;

import com.reminderapp.reminder.entity.Reminder;
import com.reminderapp.reminder.specification.UserRole;

import java.util.List;

public record UserDto(
        Long userId,
        String name,
        UserRole role,
        String email,
        String telegram
) {}
