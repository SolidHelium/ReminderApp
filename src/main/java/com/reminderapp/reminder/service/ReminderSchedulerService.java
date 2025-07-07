package com.reminderapp.reminder.service;

import com.reminderapp.reminder.entity.Reminder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Service
@AllArgsConstructor
@Slf4j
public class ReminderSchedulerService {
    private final Scheduler scheduler;

    public void scheduleReminder(Reminder reminder) {
        try {
            String jobKey = "reminder-job-" + reminder.getReminderId();
            String triggerKey = "reminder-trigger-" + reminder.getReminderId();

            JobDetail jobDetail = JobBuilder.newJob(ReminderNotificationJob.class)
                    .withIdentity(jobKey)
                    .withDescription("Reminder notification for " + reminder.getTitle())
                    .usingJobData("reminderId", reminder.getReminderId())
                    .build();

            Date scheduleTime = Date.from(reminder.getRemind().atZone(ZoneId.systemDefault()).toInstant());

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey, "reminder-triggers")
                    .withDescription("Trigger for reminder: " + reminder.getTitle())
                    .startAt(scheduleTime)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void cancelReminder(Reminder reminder) {
        try {
            String jobKey = "reminder-job-" + reminder.getReminderId();
            JobKey jobKeyObj = new JobKey(jobKey, "reminder-jobs");
            if (scheduler.checkExists(jobKeyObj)) {
                scheduler.deleteJob(jobKeyObj);
            } else {
                log.info("Couldn't find a job to delete reminder: " + reminder.getReminderId());
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Could not delete a reminder job" + e);
        }
    }

    public void rescheduleReminder(Reminder reminder) {
        try {
            cancelReminder(reminder);
            scheduleReminder(reminder);
        } catch (Exception e) {
            throw new RuntimeException("Could not reschedule reminder" + e);
        }
    }
}
