package com.reminderapp.reminder.service;

import com.reminderapp.reminder.dto.AuthResponse;
import com.reminderapp.reminder.dto.LoginRequest;

public interface AuthorizationService {
    AuthResponse login(LoginRequest loginRequest);
}
