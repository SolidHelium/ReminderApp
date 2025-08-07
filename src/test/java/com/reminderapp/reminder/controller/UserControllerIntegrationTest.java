package com.reminderapp.reminder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reminderapp.reminder.dto.ChangePasswordRequest;
import com.reminderapp.reminder.dto.CreateUserRequest;
import com.reminderapp.reminder.dto.UpdateUserRequest;
import com.reminderapp.reminder.entity.User;
import com.reminderapp.reminder.repository.UserRepository;
import com.reminderapp.reminder.security.JwtUtil;
import com.reminderapp.reminder.security.UserPrincipal;
import com.reminderapp.reminder.specification.UserRole;
import org.checkerframework.checker.units.qual.C;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@Disabled
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepo;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    private User user;
    private String token;
    private long userId;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Name");
        user.setPassword(passwordEncoder.encode("123456789"));
        user.setRole(UserRole.USER);
        user.setEmail("email");
        user.setTelegram("123456789");
        user = userRepo.save(user);
        userId = user.getUserId();

        UserPrincipal userPrincipal = new UserPrincipal(user);
        token = jwtUtil.generateToken(userPrincipal);
    }

    @Test
    void createUser_success() throws Exception {
        CreateUserRequest createRequest = new CreateUserRequest(
                "Name 2",
                "email2",
                "adminadmin",
                "987654321"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/user/createUser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Name 2"))
                .andExpect(jsonPath("$.email").value("email2"))
                .andExpect(jsonPath("$.telegram").value("987654321"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(responseContent);
        Long newUserId = node.get("userId").asLong();
        User newUser = userRepo.findById(newUserId).orElseThrow(() ->
                new RuntimeException("User not found"));

        assertThat(newUser.getName()).isEqualTo(createRequest.name());
        assertThat(newUser.getEmail()).isEqualTo(createRequest.email());
        assertThat(newUser.getTelegram()).isEqualTo(createRequest.telegram());
    }

    @Test
    void getUser_success() throws Exception {
        mockMvc.perform(get("/api/v1/user")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.telegram").value("123456789"));
    }

    @Test
    void updateUser_success() throws Exception {
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                "New name",
                "New email",
                "New telegram"
        );

        MvcResult result = mockMvc.perform(put("/api/v1/user")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New name"))
                .andExpect(jsonPath("$.email").value("New email"))
                .andExpect(jsonPath("$.telegram").value("New telegram"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(responseContent);
        Long updUserId = node.get("userId").asLong();
        User updUser = userRepo.findById(updUserId).orElseThrow(() ->
                new RuntimeException("User not found"));

        assertThat(updUser.getName()).isEqualTo(updateRequest.name());
        assertThat(updUser.getEmail()).isEqualTo(updateRequest.email());
        assertThat(updUser.getTelegram()).isEqualTo(updateRequest.telegram());
    }

    @Test
    void changePassword_success() throws Exception {
        ChangePasswordRequest changePassword = new ChangePasswordRequest(
                "123456789",
                "NewPassword"
        );

        mockMvc.perform(put("/api/v1/user/changePassword")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePassword)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_success() throws Exception {
        mockMvc.perform(delete("/api/v1/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(userRepo.findById(userId)).isEqualTo(Optional.empty());
    }

    @AfterEach
    void cleanUp() {
        userRepo.deleteAll();
    }
}
