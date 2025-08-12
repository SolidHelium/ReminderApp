package com.reminderapp.reminder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reminderapp.reminder.dto.CreateReminderRequest;
import com.reminderapp.reminder.dto.UpdateReminderRequest;
import com.reminderapp.reminder.entity.Reminder;
import com.reminderapp.reminder.entity.User;
import com.reminderapp.reminder.repository.RemindersRepository;
import com.reminderapp.reminder.repository.UserRepository;
import com.reminderapp.reminder.security.JwtUtil;
import com.reminderapp.reminder.security.UserPrincipal;
import com.reminderapp.reminder.service.ReminderSchedulerService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReminderControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private RemindersRepository reminderRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @MockitoBean private ReminderSchedulerService schedulerService;

    private User user;
    private Reminder reminder;
    private String token;
    private long remId;
    private DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Name");
        user.setPassword(passwordEncoder.encode("123456789"));
        user.setRole(UserRole.USER);
        user.setEmail("email");
        user.setTelegram("123456789");
        user = userRepo.save(user);

        UserPrincipal userPrincipal = new UserPrincipal(user);
        token = jwtUtil.generateToken(userPrincipal);

        reminder = new Reminder();
        reminder.setTitle("Title");
        reminder.setDescription("Description");
        reminder.setRemind(LocalDateTime.now().plusDays(1L).withNano(0));
        reminder.setUser(user);
        reminder = reminderRepo.save(reminder);
        remId = reminder.getReminderId();

        //formatter = DateTimeFormatter.ofPattern("");

        doNothing().when(schedulerService).scheduleReminder(any(Reminder.class));
        doNothing().when(schedulerService).rescheduleReminder(any(Reminder.class));
        doNothing().when(schedulerService).cancelReminder(any(Reminder.class));
    }

    @Test
    void createReminder_success() throws Exception {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(2L).withNano(0);
        CreateReminderRequest request = new CreateReminderRequest(
                "Title-2",
                "Description-2",
                futureTime
        );

        MvcResult result = mockMvc.perform(post("/api/v1/reminder")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Title-2"))
                .andExpect(jsonPath("$.description").value("Description-2"))
                .andExpect(jsonPath("$.reminderId").value(2))
                .andExpect(jsonPath("$.remind").value(futureTime.toString()))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(responseContent);
        Long createdReminderId = node.get("reminderId").asLong();

        Reminder newReminder = reminderRepo.findById(createdReminderId).orElseThrow(
                () -> new RuntimeException("Reminder not found"));
        assertThat(newReminder.getTitle()).isEqualTo("Title-2");
        assertThat(newReminder.getDescription()).isEqualTo("Description-2");
        assertThat(newReminder.getRemind()).isEqualTo(futureTime);
    }

    @Test
    void getReminderById_success() throws Exception {
        mockMvc.perform(get("/api/v1/reminder/" + remId)
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.reminderId").value(remId))
                .andExpect(jsonPath("$.remind").value(reminder.getRemind().toString()));
    }

    @Test
    void updateReminder_success() throws Exception {
        LocalDateTime newTime = LocalDateTime.now().plusDays(3).withNano(0);
        UpdateReminderRequest updateRequest = new UpdateReminderRequest(
                remId,
                "Updated title",
                "Updated description",
                newTime
        );
        mockMvc.perform(put("/api/v1/reminder/" + remId)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reminderId").value(remId))
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.remind").value(newTime.toString()));

        Reminder updated = reminderRepo.findById(remId).orElseThrow(
                () -> new RuntimeException("Reminder not found"));
        assertThat(updated.getTitle()).isEqualTo("Updated title");
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getRemind()).isEqualTo(newTime);
    }

    @Test
    void deleteReminder_success() throws Exception {
        mockMvc.perform(delete("/api/v1/reminder/" + remId)
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(reminderRepo.findById(remId)).isEqualTo(Optional.empty());
    }

    @Test
    void listReminders_success() throws Exception {
        Reminder secondReminder = new Reminder();
        secondReminder.setTitle("Second title");
        secondReminder.setDescription("Second description");
        secondReminder.setRemind(LocalDateTime.now().plusDays(1L).withNano(0));
        secondReminder.setUser(user);
        secondReminder = reminderRepo.save(secondReminder);
        long secondRemId = secondReminder.getReminderId();

        mockMvc.perform(get("/api/v1/reminder/list?sort=title,desc")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Title"))
                .andExpect(jsonPath("$.content[1].title").value("Second title"));
    }

    @AfterEach
    void cleanUp() {
        reminderRepo.deleteAll();
        userRepo.deleteAll();
    }
}
