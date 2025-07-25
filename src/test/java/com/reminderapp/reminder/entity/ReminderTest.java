package com.reminderapp.reminder.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderTest {
    Reminder reminder;

    @BeforeEach
    public void setUp() {
        reminder = new Reminder();
    }

    @Test
    void getterAndSetterTest() {
        User user = new User();
        reminder.setReminderId(0L);
        reminder.setTitle("Title test");
        reminder.setDescription("Description test");
        reminder.setRemind(LocalDateTime.of(2025, 6, 14, 23, 59));
        reminder.setUser(user);

        assertThat(reminder.getReminderId()).isEqualTo(0);
        assertThat(reminder.getTitle()).isEqualTo("Title test");
        assertThat(reminder.getDescription()).isEqualTo("Description test");
        assertThat(reminder.getRemind()).isEqualTo(LocalDateTime.of(2025, 6, 14, 23, 59));
        assertThat(reminder.getUser()).isEqualTo(user);
    }
}
