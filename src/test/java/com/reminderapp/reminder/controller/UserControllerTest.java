package com.reminderapp.reminder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reminderapp.reminder.dto.CreateUserRequest;
import com.reminderapp.reminder.dto.UserDto;
import com.reminderapp.reminder.security.JwtRequestFilter;
import com.reminderapp.reminder.service.UserService;
import com.reminderapp.reminder.specification.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @MockitoBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    void createUser_success() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Name", "email", "password", "telegram");
        UserDto userDto = new UserDto(1L, "Name", UserRole.USER, "email", "telegram");

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/v1/user/createUser")
                //.with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/user/1"));

        verify(userService).createUser(any(CreateUserRequest.class));
    }
}