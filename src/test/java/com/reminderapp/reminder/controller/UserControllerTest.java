package com.reminderapp.reminder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reminderapp.reminder.dto.ChangePasswordRequest;
import com.reminderapp.reminder.dto.CreateUserRequest;
import com.reminderapp.reminder.dto.UpdateUserRequest;
import com.reminderapp.reminder.dto.UserDto;
import com.reminderapp.reminder.security.JwtRequestFilter;
import com.reminderapp.reminder.service.UserService;
import com.reminderapp.reminder.specification.UserRole;
import org.apache.tomcat.util.buf.UEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    private UserDto userDto;
    private final String LOGIN = "email";
    private Principal mockPrincipal;


    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "Name", UserRole.USER, "email", "telegram");
        mockPrincipal = () -> LOGIN;
    }

    @Test
    void createUser_success() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "Name",
                "email",
                "password",
                "telegram"
        );
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/v1/user/createUser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/user/1"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.email").value("email"));

        verify(userService).createUser(any(CreateUserRequest.class));
    }

    @Test
    void getUser_success() throws Exception{
        when(userService.getUserByLogin(LOGIN)).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/user")
                        .principal(mockPrincipal))
                .andExpect(status().isOk());

        verify(userService).getUserByLogin(LOGIN);
    }

    @Test
    void updateUser_success() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest(
                "new name",
                "new email",
                "new telegram"
        );

        UserDto updatedUserDto = UserDto.builder()
                .userId(1L).name("new name")
                .role(UserRole.USER)
                .email("new email")
                .telegram("new telegram")
                .build();

        when(userService.updateUser(request, LOGIN)).thenReturn(updatedUserDto);

        mockMvc.perform(put("/api/v1/user")
                    .principal(mockPrincipal)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.name").value("new name"))
                .andExpect(jsonPath("$.email").value("new email"))
                .andExpect(jsonPath("$.telegram").value("new telegram"));

        verify(userService).updateUser(request, LOGIN);
    }

    @Test
    void deleteUser_success() throws Exception {
        doNothing().when(userService).deleteUser(LOGIN);

        mockMvc.perform(delete("/api/v1/user").principal(mockPrincipal))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(LOGIN);
    }

    @Test
    void changePassword_success() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "oldPassword",
                "newPassword"
        );
        doNothing().when(userService).changePassword(LOGIN, changePasswordRequest);

        mockMvc.perform(put("/api/v1/user/changePassword")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isNoContent());

        verify(userService).changePassword(LOGIN, changePasswordRequest);
    }
}