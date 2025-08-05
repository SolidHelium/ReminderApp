package com.reminderapp.reminder.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.reminderapp.reminder.config.SecurityConfig;
import com.reminderapp.reminder.dto.CreateReminderRequest;
import com.reminderapp.reminder.dto.ReminderDto;
import com.reminderapp.reminder.dto.UpdateReminderRequest;
import com.reminderapp.reminder.entity.Reminder;
import com.reminderapp.reminder.security.JwtRequestFilter;
import com.reminderapp.reminder.service.ReminderService;
import com.reminderapp.reminder.service.ReminderServiceImpl;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReminderController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReminderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ReminderService reminderService;
    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private ObjectMapper objectMapper;

    private final String USER_EMAIL = "validemail@email.com";
    private Principal mockPrincipal;
    private LocalDateTime remind;
    private ReminderDto reminderDto;

    @BeforeEach
    void setUp() {
        mockPrincipal = () -> USER_EMAIL;
        remind = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        reminderDto = new ReminderDto(1L, 1L, "title", "description", remind);
    }

    @Test
    void createReminder_returnsReminder() throws Exception {
        CreateReminderRequest request = new CreateReminderRequest("title", "description", remind);

        when(reminderService.createReminder(any(CreateReminderRequest.class), eq(USER_EMAIL))).thenReturn(reminderDto);

        mockMvc.perform(post("/api/v1/reminder")
                    .principal(mockPrincipal)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/reminder/1"))
                .andExpect(jsonPath("$.reminderId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.remind").exists());

        verify(reminderService).createReminder(any(CreateReminderRequest.class), eq(USER_EMAIL));
    }

    @Test
    void getById_returnsReminder() throws Exception {
        when(reminderService.getReminderById(1L, USER_EMAIL)).thenReturn(reminderDto);

        mockMvc.perform(get("/api/v1/reminder/1")
                    .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reminderId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.remind").exists());

        verify(reminderService).getReminderById(1L, USER_EMAIL);
    }

    @Test
    void updateById_success() throws Exception {
        UpdateReminderRequest updateRequest = new UpdateReminderRequest(
                1L,
                "Updated title",
                "Updated description",
                remind
        );
        ReminderDto updatedDto = new ReminderDto(
                1L,
                1L,
                "Updated title",
                "Updated description",
                remind
        );
        when(reminderService.updateReminder(updateRequest, USER_EMAIL)).thenReturn(updatedDto);

        mockMvc.perform(put("/api/v1/reminder/1")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reminderId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.remind").exists());

        verify(reminderService).updateReminder(updateRequest, USER_EMAIL);
    }

    @Test
    void deleteById_success() throws Exception {
        doNothing().when(reminderService).deleteReminder(1L, USER_EMAIL);

        mockMvc.perform(delete("/api/v1/reminder/1").principal(mockPrincipal))
                .andExpect(status().isNoContent());

        verify(reminderService).deleteReminder(1L, USER_EMAIL);
    }

    @Test
    void list_success() throws Exception {
        String search = "description";
        LocalDateTime to = LocalDateTime.now().plusDays(2);
        LocalDateTime from = LocalDateTime.now().minusDays(2);

        ReminderDto reminderDto2 = new ReminderDto(2L, 1L, "title", "description", remind);

        List<ReminderDto> reminderList = new ArrayList<>();
        reminderList.add(reminderDto);
        reminderList.add(reminderDto2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("remind"));
        Page<ReminderDto> page = new PageImpl<>(reminderList, pageable, reminderList.size());

        when(reminderService.findAll(USER_EMAIL, search, from, to, pageable)).thenReturn(page);

        mockMvc.perform(get("/api/v1/reminder/list")
                    .param("from", String.valueOf(from))
                    .param("to", String.valueOf(to))
                    .param("search", "description")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sort", "remind")
                    .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].reminderId").value(1));

        verify(reminderService).findAll(USER_EMAIL, search, from, to, pageable);
    }
}