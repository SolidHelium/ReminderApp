package com.reminderapp.reminder.service;

import com.reminderapp.reminder.dto.CreateReminderRequest;
import com.reminderapp.reminder.dto.ReminderDto;
import com.reminderapp.reminder.dto.UpdateReminderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


public interface ReminderService {
    ReminderDto createReminder(CreateReminderRequest request, String userLogin);
    ReminderDto updateReminder(UpdateReminderRequest request, String userLogin);
    ReminderDto getReminderById(long id, String userLogin);
    void deleteReminder(long id, String userLogin);
    Page<ReminderDto> findAll(String userLogin, String search, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
