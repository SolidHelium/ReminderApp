package com.reminderapp.reminder.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.reminderapp.reminder.config.SecurityConfig;
import com.reminderapp.reminder.dto.CreateReminderRequest;
import com.reminderapp.reminder.dto.ReminderDto;
import com.reminderapp.reminder.security.JwtRequestFilter;
import com.reminderapp.reminder.service.ReminderService;
import com.reminderapp.reminder.service.ReminderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(ReminderController.class)
@AutoConfigureMockMvc(addFilters = false)
//@Import(SecurityConfig.class)
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

    @Test
    //@WithMockUser(username = USER_EMAIL)
    void createReminder_returnsReminder() throws Exception {
        Principal mockPrincipal = () -> USER_EMAIL;
        LocalDateTime remind = LocalDateTime.now().plusDays(1);
        CreateReminderRequest request = new CreateReminderRequest("title", "description", remind);
        ReminderDto reminderDto = new ReminderDto(1L, 1L, "title", "description", remind);

        when(reminderService.createReminder(any(CreateReminderRequest.class), eq(USER_EMAIL))).thenReturn(reminderDto);

        mockMvc.perform(post("/api/v1/reminder")
                    //.with(csrf())
                    .principal(mockPrincipal)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/reminder/1"));

        verify(reminderService).createReminder(any(CreateReminderRequest.class), eq(USER_EMAIL));
    }

}