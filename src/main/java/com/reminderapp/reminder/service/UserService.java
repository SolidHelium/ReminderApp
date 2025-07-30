package com.reminderapp.reminder.service;

import com.reminderapp.reminder.dto.ChangePasswordRequest;
import com.reminderapp.reminder.dto.CreateUserRequest;
import com.reminderapp.reminder.dto.UpdateUserRequest;
import com.reminderapp.reminder.dto.UserDto;

public interface UserService {
    UserDto createUser(CreateUserRequest request);
    UserDto getUserByLogin(String userLogin);
    UserDto updateUser(UpdateUserRequest request, String userLogin);
    void changePassword(String userLogin, ChangePasswordRequest request);
    void deleteUser(String userLogin);
}
