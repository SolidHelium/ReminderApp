package com.reminderapp.reminder.controller;
import com.reminderapp.reminder.entity.Reminder;
import com.reminderapp.reminder.entity.User;
import com.reminderapp.reminder.repository.RemindersRepository;
import com.reminderapp.reminder.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/repo")
public class UserController {
    private final UserRepository userRepo;
    private final RemindersRepository remRepo;

    public UserController(UserRepository repository, RemindersRepository remindersRepository) {
        this.remRepo = remindersRepository;
        this.userRepo = repository;
    }

    @GetMapping("/get")
    public void get() {}

    @PostMapping("/post")
    public String post() {
        User user = new User();
        user.setName("Sergei");
        user.setEmail("sergei@gmail.com");
        user.setPassword("sergeiPassword");
        user.setTelegram("@sergeiTelegram");
        userRepo.save(user);

        Reminder reminder = new Reminder();
        reminder.setUser(user);
        reminder.setTitle("sergeiTitle");
        reminder.setDescription("sergeiDescription");
        reminder.setRemind_date(LocalDateTime.now().plusMonths(1));
        remRepo.save(reminder);

        long i = userRepo.count();
        long y = remRepo.count();

        return "Users: " + i + "Reminders: " + y + "/n" + reminder + "/n" + user;
    }

}
