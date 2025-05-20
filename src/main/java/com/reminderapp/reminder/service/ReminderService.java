package com.reminderapp.reminder.service;

import com.reminderapp.reminder.dto.CreateReminderRequest;
import com.reminderapp.reminder.dto.ReminderDto;
import com.reminderapp.reminder.dto.UpdateReminderRequest;

public interface ReminderService {
    ReminderDto createReminder(CreateReminderRequest request);
    ReminderDto updateReminder(UpdateReminderRequest request);
    ReminderDto getReminderById(long id);
    void deleteReminder(long id);
}
