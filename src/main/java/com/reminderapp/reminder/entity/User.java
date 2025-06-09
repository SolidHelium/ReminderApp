package com.reminderapp.reminder.entity;

import com.reminderapp.reminder.specification.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userId;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    //TODO: Map enums using converter
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole role = UserRole.USER;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "telegram")
    private String telegram;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reminder> reminders;

    @Override
    public String toString() {
        return "User{" +
                ", name='" + name + '\'' +
                ", id=" + userId + '\'' +
                ", role=" + role.getAuthority() + '\'' +
                ", email='" + email + '\'' +
                "telegram='" + telegram + '\'' +
                '}';
    }

}
