package com.reminderapp.reminder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reminderapp.reminder.dto.LoginRequest;
import com.reminderapp.reminder.entity.User;
import com.reminderapp.reminder.repository.UserRepository;
import com.reminderapp.reminder.security.JwtUtil;
import com.reminderapp.reminder.specification.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Disabled
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepo;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    //@Autowired private JwtUtil jwtUtil;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Name");
        user.setPassword(passwordEncoder.encode("123456789"));
        user.setRole(UserRole.USER);
        user.setEmail("email");
        user.setTelegram("123456789");
        user = userRepo.save(user);
    }

    @Test
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest(
                "email",
                "123456789"
        );

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authToken").isNotEmpty());
    }

    @AfterEach
    void cleanUp() {
        userRepo.deleteAll();
    }
}
