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
import java.security.Principal;

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

    @GetMapping
    public ResponseEntity<UserDto> getUser(Principal principal) {
        return ResponseEntity.ok(userService.getUserByLogin(principal.getName()));
    }

    @PutMapping
    public ResponseEntity<UserDto> updateUser(Principal principal, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(request, principal.getName()));
    }

    @PutMapping("/changePassword")
    public ResponseEntity<Void> changePassword(Principal principal, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(Principal principal) {
        userService.deleteUser(principal.getName());
        return ResponseEntity.noContent().build();
    }

// FOR ADMINS
//    @GetMapping("/{id}")
//    public ResponseEntity<UserDto> getUser(@PathVariable long id) {
//        return ResponseEntity.ok(userService.getUserById(id));
//    }

//@PutMapping("/{id}")
//public ResponseEntity<UserDto> updateUser(@PathVariable long id, @RequestBody UpdateUserRequest request) {
//    return ResponseEntity.ok(userService.updateUser(request, id));
//}

}
