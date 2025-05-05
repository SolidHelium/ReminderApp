package com.reminderapp.reminder.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "remind")
    private LocalDateTime remind_date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", remind_date=" + remind_date +
                ", user=" + user +
                '}';
    }

    //Getters and Setters

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getRemind_date() {
        return remind_date;
    }
    public void setRemind_date(LocalDateTime remind_date) {
        this.remind_date = remind_date;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
}
