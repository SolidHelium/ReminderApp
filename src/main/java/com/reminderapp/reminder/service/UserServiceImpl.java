package com.reminderapp.reminder.service;

import com.reminderapp.reminder.dto.ChangePasswordRequest;
import com.reminderapp.reminder.dto.CreateUserRequest;
import com.reminderapp.reminder.dto.UpdateUserRequest;
import com.reminderapp.reminder.dto.UserDto;
import com.reminderapp.reminder.entity.User;
import com.reminderapp.reminder.repository.UserRepository;
import com.reminderapp.reminder.service.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    //TODO: Proper exception handling

    @Override
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("User with email " + request.email() + " already exists");
        }
        if (request.email() == null || request.email().isEmpty()) {
            throw new RuntimeException("Email cannot be empty");
        }
        if (request.password() == null || request.password().length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }
        User user = userMapper.createUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        User created = userRepository.save(user);
        return userMapper.toDto(created);
    }

    @Override
    public UserDto updateUser(UpdateUserRequest request, long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
        userMapper.updateUser(request, user);
        User updated = userRepository.save(user);
        return userMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
        return userMapper.toDto(user);
    }

    @Override
    public void deleteUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User already doesn't exist"));
        userRepository.deleteById(id);
    }

    @Override
    public void changePassword(long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new RuntimeException("Password does not match");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
