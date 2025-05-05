package com.reminderapp.reminder.service.mapper;

import com.reminderapp.reminder.entity.Reminder;
import com.reminderapp.reminder.entity.User;
import com.reminderapp.reminder.service.dto.CreateReminderRequest;
import com.reminderapp.reminder.service.dto.ReminderDto;

public class ReminderMapper {
    public static Reminder toEntity(CreateReminderRequest createRemReq, User user) {
        Reminder reminder = new Reminder();

        reminder.setTitle(createRemReq.title());
        reminder.setDescription(createRemReq.description());
        reminder.setRemind_date(createRemReq.remind());
        reminder.setUser(user);

        return reminder;
    }

    public static ReminderDto toDto(Reminder reminder) {
        return new ReminderDto(
                reminder.getId(),
                reminder.getUser().getId(),
                reminder.getTitle(),
                reminder.getDescription(),
                reminder.getRemind_date()
                );
    }
}
