package com.reminderapp.reminder.repository;

import com.reminderapp.reminder.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RemindersRepository extends JpaRepository<Reminder, Long>, JpaSpecificationExecutor<Reminder> {
}
