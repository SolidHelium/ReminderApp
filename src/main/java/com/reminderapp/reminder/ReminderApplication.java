package com.reminderapp.reminder;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class ReminderApplication {

    public static void main(String[] args) {
//        Dotenv dotenv = Dotenv.configure().directory("./").filename(".env").load();
//
//        dotenv.entries().forEach(entry ->
//                System.setProperty(entry.getKey(), entry.getValue())
//        );

        SpringApplication.run(ReminderApplication.class, args);
    }

}
