package com.reminderapp.reminder.service;

import com.reminderapp.reminder.entity.Reminder;
import com.reminderapp.reminder.entity.User;
import com.reminderapp.reminder.repository.RemindersRepository;
import com.reminderapp.reminder.repository.UserRepository;
import com.reminderapp.reminder.service.dto.CreateReminderRequest;
import com.reminderapp.reminder.service.dto.ReminderDto;
import com.reminderapp.reminder.service.dto.UpdateReminderRequest;
import com.reminderapp.reminder.service.mapper.ReminderMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReminderServiceImpl implements ReminderService {
    private final UserRepository userRepo;
    private final RemindersRepository reminderRepo;

    public ReminderServiceImpl(UserRepository userRepo, RemindersRepository reminderRepo) {
        this.userRepo = userRepo;
        this.reminderRepo = reminderRepo;
    }

    @Override
    public ReminderDto createReminder(CreateReminderRequest request) {
        User user = userRepo.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.remind().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reminder date and time should be in the future");
        }

        Reminder reminder = ReminderMapper.toEntity(request, user);
        Reminder saved = reminderRepo.save(reminder);

        return ReminderMapper.toDto(saved);
    }

    @Override
    public ReminderDto updateReminder(UpdateReminderRequest request) {
        Reminder reminder = reminderRepo.findById(request.reminderId())
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        reminder.setTitle(request.title());
        reminder.setDescription(request.description());
        reminder.setRemind_date(request.remind());

        Reminder updated = reminderRepo.save(reminder);

        return ReminderMapper.toDto(updated);
    }

    @Override
    public ReminderDto getReminderById(long id) {
        Reminder reminder = reminderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        return ReminderMapper.toDto(reminder);
    }

    @Override
    public void deleteReminder(long id) {
        Reminder reminder = reminderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder does not exist"));
        reminderRepo.deleteById(id);
    }
}
