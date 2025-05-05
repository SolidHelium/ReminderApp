package com.reminderapp.reminder.service;

import com.reminderapp.reminder.service.dto.CreateReminderRequest;
import com.reminderapp.reminder.service.dto.ReminderDto;
import com.reminderapp.reminder.service.dto.UpdateReminderRequest;

public interface ReminderService {
    ReminderDto createReminder(CreateReminderRequest request);
    ReminderDto updateReminder(UpdateReminderRequest request);
    ReminderDto getReminderById(long id);
    void deleteReminder(long id);

}
