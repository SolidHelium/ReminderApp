package com.reminderapp.reminder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reminderapp.reminder.dto.AuthResponse;
import com.reminderapp.reminder.dto.LoginRequest;
import com.reminderapp.reminder.security.JwtRequestFilter;
import com.reminderapp.reminder.service.AuthorizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @MockitoBean private AuthorizationService   authService;
    @MockitoBean private JwtRequestFilter       jwtRequestFilter;
    @Autowired   private MockMvc                mockMvc;
    @Autowired   private ObjectMapper           objectMapper;
    @Autowired   private AuthorizationService   authorizationService;

    @Test
    void login_success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("email", "password");
        AuthResponse authResponse = new AuthResponse("auth token");

        when(authorizationService.login(loginRequest)).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

        verify(authorizationService).login(loginRequest);
    }
}