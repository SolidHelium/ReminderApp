package com.reminderapp.reminder.controller;
import com.reminderapp.reminder.dto.ChangePasswordRequest;
import com.reminderapp.reminder.dto.CreateUserRequest;
import com.reminderapp.reminder.dto.UpdateUserRequest;
import com.reminderapp.reminder.dto.UserDto;
import com.reminderapp.reminder.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;


    @PostMapping("/createUser")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request) {
        UserDto userDto = userService.createUser(request);
        URI location = URI.create("/api/v1/user/" + userDto.userId());
        return ResponseEntity.created(location).body(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(request, id));
    }

    @PutMapping("/{id}/changePassword")
    public ResponseEntity<Void> changePassword(@PathVariable long id, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

}
