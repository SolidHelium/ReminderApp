package com.reminderapp.reminder.service;

import com.reminderapp.reminder.entity.Reminder;
import com.reminderapp.reminder.repository.RemindersRepository;
import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ReminderNotificationJob implements Job {

    private final AllTypesNotificationSenderService notificationSenderService;
    private final RemindersRepository remindersRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Long reminderId = jobDataMap.getLong("reminderId");

        try {
            Optional<Reminder> reminderOptional = remindersRepository.findById(reminderId);

            if (reminderOptional.isEmpty()) {return;}

            Reminder reminder = reminderOptional.get();
            notificationSenderService.sendAllNotifications(reminder);

        } catch (Exception e) {
            throw new RuntimeException("Can't find reminder to execute", e);
        }
    }
}
