package com.reminderapp.reminder;

import com.reminderapp.reminder.dto.*;
import com.reminderapp.reminder.entity.Reminder;
import com.reminderapp.reminder.entity.User;
import com.reminderapp.reminder.repository.UserRepository;
import com.reminderapp.reminder.service.ReminderSchedulerService;
import com.reminderapp.reminder.service.ReminderService;
import com.reminderapp.reminder.specification.UserRole;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReminderAppFunctionalTests {

    @LocalServerPort
    private int port;

    private String baseUrl;
    private static long userId;
    private static long reminderId;
    private static String authToken;
    private HttpHeaders header;

    @Container
    private static final PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private ReminderSchedulerService schedulerService;

    @DynamicPropertySource
    public static void configurePropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.datasource.hikari.connection-timeout", () -> "3000");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        header = new HttpHeaders();
        header.setBearerAuth(authToken);
        header.setContentType(MediaType.APPLICATION_JSON);

        doNothing().when(schedulerService).scheduleReminder(any(Reminder.class));
        doNothing().when(schedulerService).rescheduleReminder(any(Reminder.class));
        doNothing().when(schedulerService).cancelReminder(any(Reminder.class));
    }

    @Test
    @Order(1)
    void createUser() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .name("Name")
                .email("email")
                .password("password")
                .build();

        ResponseEntity<UserDto> response = restTemplate.postForEntity(
                baseUrl + "/api/v1/user/createUser",
                createUserRequest,
                UserDto.class
        );
        UserDto userDto = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(userDto).isNotNull();
        assertEquals(userDto.name(), createUserRequest.name());
        assertEquals(userDto.email(), createUserRequest.email());

        userId = userDto.userId();
    }

    @Test
    @Order(2)
    void authenticateUser() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("email")
                .password("password")
                .build();

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                baseUrl + "/auth/login",
                loginRequest,
                AuthResponse.class
        );

        AuthResponse authResponse = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(authResponse.authToken()).isNotNull();
        assertThat(authResponse.authToken()).isNotBlank();

        authToken = authResponse.authToken();
    }

    @Test
    @Order(3)
    void getUser() {
        HttpEntity<String> entity = new HttpEntity<>(header);

        ResponseEntity<UserDto> response = restTemplate.exchange(
                baseUrl + "/api/v1/user",
                HttpMethod.GET,
                entity,
                UserDto.class
        );

        UserDto userDto = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(userDto).isNotNull();
        assertEquals(userDto.userId(), userId);
        assertEquals(userDto.email(), "email");
        assertEquals(userDto.name(), "Name");
        assertEquals(userDto.role(), UserRole.USER);
    }

    @Test
    @Order(4)
    void changePassword() {
        ChangePasswordRequest changePassword = ChangePasswordRequest.builder()
                .oldPassword("password")
                .newPassword("new_password")
                .build();

        HttpEntity<ChangePasswordRequest> entity = new HttpEntity<>(changePassword, header);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/api/v1/user/changePassword",
                HttpMethod.PUT,
                entity,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(5)
    void updateUser() {
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("New name")
                .email("newemail")
                .telegram("new_telegram")
                .build();

        HttpEntity<UpdateUserRequest> entity = new HttpEntity<>(updateRequest, header);

        ResponseEntity<UserDto> response = restTemplate.exchange(
                baseUrl + "/api/v1/user",
                HttpMethod.PUT,
                entity,
                UserDto.class
        );

        UserDto userDto = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(userDto).isNotNull();
        assertEquals(userDto.userId(), userId);
        assertEquals(userDto.email(), "newemail");
        assertEquals(userDto.name(), "New name");
        assertEquals(userDto.telegram(), "new_telegram");
        assertEquals(userDto.role(), UserRole.USER);
    }

    @Test
    @Order(6)
    void reauthenticate() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("newemail")
                .password("new_password")
                .build();

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                baseUrl + "/auth/login",
                loginRequest,
                AuthResponse.class
        );

        AuthResponse authResponse = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(authResponse.authToken()).isNotNull();
        assertThat(authResponse.authToken()).isNotBlank();

        authToken = authResponse.authToken();
    }

    @Test
    @Order(7)
    void createReminder() {
        LocalDateTime remind = LocalDateTime.now().plusDays(1);
        CreateReminderRequest createReminderRequest = CreateReminderRequest.builder()
                .title("Title")
                .description("Description")
                .remind(remind)
                .build();

        HttpEntity<CreateReminderRequest> entity = new HttpEntity<>(createReminderRequest, header);

        ResponseEntity<ReminderDto> response = restTemplate.exchange(
                baseUrl + "/api/v1/reminder",
                HttpMethod.POST,
                entity,
                ReminderDto.class
        );

        ReminderDto reminderDto = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(reminderDto).isNotNull();
        assertEquals(reminderDto.title(), "Title");
        assertEquals(reminderDto.description(), "Description");

        reminderId = reminderDto.reminderId();
    }

    @Test
    @Order(8)
    void getReminder() {
        HttpEntity<String> entity = new HttpEntity<>(header);

        ResponseEntity<ReminderDto> response = restTemplate.exchange(
                baseUrl + "/api/v1/reminder/" + reminderId,
                HttpMethod.GET,
                entity,
                ReminderDto.class
        );

        ReminderDto reminderDto = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(reminderDto).isNotNull();
        assertEquals(reminderDto.title(), "Title");
        assertEquals(reminderDto.description(), "Description");
    }

    @Test
    @Order(9)
    void updateReminder() {
        UpdateReminderRequest updateRequest = UpdateReminderRequest.builder()
                .reminderId(2L)
                .title("New title")
                .description("New description")
                .remind(LocalDateTime.now().plusDays(2))
                .build();

        HttpEntity<UpdateReminderRequest> entity = new HttpEntity<>(updateRequest, header);

        ResponseEntity<ReminderDto> response = restTemplate.exchange(
                baseUrl + "/api/v1/reminder/" + reminderId,
                HttpMethod.PUT,
                entity,
                ReminderDto.class
        );

        ReminderDto reminderDto = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(reminderDto).isNotNull();
        assertEquals(reminderDto.title(), "New title");
        assertEquals(reminderDto.description(), "New description");
    }

    @Test
    @Order(10)
    void listReminders() {
        HttpEntity<String> entity = new HttpEntity<>(header);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/api/v1/reminder/list?search=New&page=0&size=3",
                HttpMethod.GET,
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(11)
    void deleteReminder() {
        HttpEntity<String> entity = new HttpEntity<>(header);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/api/v1/reminder/" + reminderId,
                HttpMethod.DELETE,
                entity,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<ReminderDto> response2 = restTemplate.exchange(
                baseUrl + "/api/v1/reminder/" + reminderId,
                HttpMethod.GET,
                entity,
                ReminderDto.class
        );

        ReminderDto reminderDto = response2.getBody();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertThat(reminderDto.title()).isNull();
        assertThat(reminderDto.description()).isNull();
        assertThat(reminderDto.remind()).isNull();
    }

    @Test
    @Order(12)
    void deleteUser() {
        HttpEntity<String> entity = new HttpEntity<>(header);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/api/v1/user",
                HttpMethod.DELETE,
                entity,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<UserDto> response2 = restTemplate.exchange(
                baseUrl + "/api/v1/user" + reminderId,
                HttpMethod.GET,
                entity,
                UserDto.class
        );

        UserDto userDto = response2.getBody();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertThat(userDto).isNull();
    }
}
