package com.reminderapp.reminder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminder")
@Getter
@Setter
@NoArgsConstructor
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long reminderId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "remind")
    private LocalDateTime remind;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + reminderId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", remind_date=" + remind +
                ", user=" + user.getUserId() + " " + user.getName() +
                '}';
    }

}
