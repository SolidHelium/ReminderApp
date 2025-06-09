package com.reminderapp.reminder.service;

import com.reminderapp.reminder.dto.CreateUserRequest;
import com.reminderapp.reminder.dto.UpdateUserRequest;
import com.reminderapp.reminder.dto.UserDto;

public interface UserService {
    UserDto createUser(CreateUserRequest request);
    UserDto updateUser(UpdateUserRequest request, long id);
    UserDto getUserById(long id);
    void deleteUser(long id);
    void changePassword(long id, String oldPassword, String newPassword);
}
