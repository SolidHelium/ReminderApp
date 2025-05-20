package com.reminderapp.reminder.controller;
import com.reminderapp.reminder.repository.RemindersRepository;
import com.reminderapp.reminder.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserRepository userRepo;
    private final RemindersRepository remRepo;

    public UserController(UserRepository userRepository, RemindersRepository remindersRepository) {
        this.userRepo = userRepository;
        this.remRepo = remindersRepository;
    }

    @GetMapping("/getUser")
    public void get() {}

    @PostMapping("/postUser")
    public void post() {
    }

}
