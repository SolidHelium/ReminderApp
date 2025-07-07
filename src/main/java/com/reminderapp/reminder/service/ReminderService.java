package com.reminderapp.reminder.service;

import com.reminderapp.reminder.dto.CreateReminderRequest;
import com.reminderapp.reminder.dto.ReminderDto;
import com.reminderapp.reminder.dto.UpdateReminderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


public interface ReminderService {
    ReminderDto createReminder(CreateReminderRequest request);
    ReminderDto updateReminder(UpdateReminderRequest request);
    ReminderDto getReminderById(long id);
    void deleteReminder(long id);
    Page<ReminderDto> findAll(String search, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
