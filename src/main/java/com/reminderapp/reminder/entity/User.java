package com.reminderapp.reminder.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "telegram")
    private String telegram;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reminder> reminders;

    @Override
    public String toString() {
        return "User{" +
                "telegram='" + telegram + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }

    //Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }
}
