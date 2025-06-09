package com.reminderapp.reminder.dto;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {}
