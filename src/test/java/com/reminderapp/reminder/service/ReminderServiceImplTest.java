package com.reminderapp.reminder.service;

import com.reminderapp.reminder.dto.CreateReminderRequest;
import com.reminderapp.reminder.dto.ReminderDto;
import com.reminderapp.reminder.dto.UpdateReminderRequest;
import com.reminderapp.reminder.entity.Reminder;
import com.reminderapp.reminder.entity.User;
import com.reminderapp.reminder.repository.RemindersRepository;
import com.reminderapp.reminder.repository.UserRepository;
import com.reminderapp.reminder.service.mapper.ReminderMapper;
import com.reminderapp.reminder.specification.UserRole;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReminderService tests")
class ReminderServiceImplTest {
    @Mock private RemindersRepository reminderRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReminderMapper reminderMapper;
    @Mock private ReminderSchedulerService reminderSchedulerService;

    @InjectMocks private ReminderServiceImpl reminderService;

    private Reminder reminder;
    private User user;
    private final LocalDateTime futureTime = LocalDateTime.now().plusDays(1L);
    private final LocalDateTime pastTime = LocalDateTime.now().minusDays(1L);
    private final String userLogin = "validemail@email.com";

    private final ReminderDto reminderDto = new ReminderDto(
            1L,
            1L,
            "Title",
            "Description",
            futureTime
    );

    @BeforeEach
    void setUp() {
        user = createTestUser(
                1L,
                "testUser",
                "valid_password",
                UserRole.USER,
                "validemail@email.com",
                "123456789");
        reminder = createTestReminder(
                1L,
                "Title",
                "Description",
                futureTime,
                user);
    }

    private Reminder createTestReminder(long id, String title, String description, LocalDateTime remind, User remUser) {
        Reminder newReminder = new Reminder();
        newReminder.setReminderId(id);
        newReminder.setTitle(title);
        newReminder.setDescription(description);
        newReminder.setRemind(remind);
        newReminder.setUser(remUser);
        return newReminder;
    }
    private User createTestUser(long id, String name, String password, UserRole role, String email, String telegram) {
        User newUser = new User();
        newUser.setUserId(id);
        newUser.setName(name);
        newUser.setPassword(password);
        newUser.setRole(role);
        newUser.setEmail(email);
        newUser.setTelegram(telegram);
        return newUser;
    }

    @Nested
    class CreateReminderTests {
        private final CreateReminderRequest createReminderRequest = new CreateReminderRequest(
                "Title",
                "Description",
                futureTime
        );

        @Test
        void reminderCreated_validRequest() {
            when(reminderRepository.save(reminder)).thenReturn(reminder);
            when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));
            when(reminderMapper.toEntity(createReminderRequest, user)).thenReturn(reminder);
            when(reminderMapper.toDto(reminder)).thenReturn(reminderDto);

            ReminderDto result = reminderService.createReminder(createReminderRequest, "email");
            assertThat(result).isNotNull().isEqualTo(reminderDto);

            verify(userRepository).findByEmail("email");
            verify(reminderMapper).toEntity(createReminderRequest, user);
            verify(reminderRepository).save(reminder);
            verify(reminderSchedulerService).scheduleReminder(reminder);
            verify(reminderMapper).toDto(reminder);
        }

        @Test
        void userNotFound_ExceptionThrown() {
            when(userRepository.findByEmail("email")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reminderService.createReminder(createReminderRequest, "email"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User not found");

            verify(userRepository).findByEmail("email");
            verifyNoInteractions(reminderMapper, reminderRepository, reminderSchedulerService);
        }

        @Test
        void invalidTime_ExceptionThrown() {
            CreateReminderRequest pastRequest = new CreateReminderRequest(
                    "Title",
                    "Description",
                    pastTime
            );
            when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> reminderService.createReminder(pastRequest, "email"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Reminder date and time should be in the future");

            verify(userRepository).findByEmail("email");
            verifyNoInteractions(reminderMapper, reminderRepository, reminderSchedulerService);
        }

    }

    @Nested
    class UpdateReminderTests {
        private final LocalDateTime updateTime = futureTime.plusDays(1);
        private final UpdateReminderRequest updateReminderRequest = new UpdateReminderRequest(
                1L,
                "Updated title",
                "Updated description",
                updateTime
        );

        @Test
        void reminderUpdated_ValidRequest() {
            when(reminderRepository.findById(updateReminderRequest.reminderId())).thenReturn(Optional.of(reminder));
            ReminderDto updatedDto = new ReminderDto(
                    1L,
                    1L,
                    "Updated title",
                    "Updated description",
                    updateTime
            );
            when(reminderRepository.save(reminder)).thenReturn(reminder);
            when(reminderMapper.toDto(reminder)).thenReturn(updatedDto);

            ReminderDto result = reminderService.updateReminder(updateReminderRequest, userLogin);
            assertThat(result).isNotNull().isEqualTo(updatedDto);

            verify(reminderRepository).findById(updateReminderRequest.reminderId());
            verify(reminderMapper).updateEntity(updatedDto, reminder);
            verify(reminderRepository).save(reminder);
            verify(reminderSchedulerService).rescheduleReminder(reminder);
            verify(reminderMapper).toDto(reminder);
        }

        @Test
        void reminderNotFound_ExceptionThrown() {
            when(reminderRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reminderService.updateReminder(updateReminderRequest, userLogin))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Reminder not found");

            verify(reminderRepository).findById(1L);
            verifyNoInteractions(reminderMapper, reminderSchedulerService);
            verifyNoMoreInteractions(reminderRepository);
        }

        @Test
        void invalidTime_ExceptionThrown() {
            UpdateReminderRequest invalidTimeUpdateRequest = new UpdateReminderRequest(
                    1L,
                    "Updated title",
                    "Updated description",
                    pastTime
            );

            when(reminderRepository.findById(invalidTimeUpdateRequest.reminderId())).thenReturn(Optional.of(reminder));

            assertThatThrownBy(() -> reminderService.updateReminder(invalidTimeUpdateRequest, userLogin))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Reminder date and time should be in the future");

            verify(reminderRepository).findById(invalidTimeUpdateRequest.reminderId());
            verifyNoMoreInteractions(reminderRepository);
            verifyNoInteractions(reminderMapper, reminderSchedulerService);
        }
    }

    @Nested
    class GetReminderByIdTests {

        @Test
        void getById_Success() {
            when(reminderRepository.findById(1L)).thenReturn(Optional.of(reminder));
            when(reminderMapper.toDto(reminder)).thenReturn(reminderDto);

            ReminderDto result = reminderService.getReminderById(1L, userLogin);
            assertThat(result).isNotNull().isEqualTo(reminderDto);

            verify(reminderRepository).findById(1L);
            verify(reminderMapper).toDto(reminder);
        }

        @Test
        void reminderNotFound_ExceptionThrown() {
            when(reminderRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reminderService.getReminderById(1L, userLogin))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Reminder not found");

            verify(reminderRepository).findById(1L);
            verifyNoInteractions(reminderMapper);
        }
    }

    @Nested
    class DeleteReminderTests {

        @Test
        void reminderDeleted_Success() {
            when(reminderRepository.findById(1L)).thenReturn(Optional.of(reminder));

            reminderService.deleteReminder(1L, userLogin);

            verify(reminderRepository).findById(1L);
            verify(reminderRepository).deleteById(1L);
            verify(reminderSchedulerService).cancelReminder(reminder);
        }

        @Test
        void reminderNotFound_ExceptionThrown() {
            when(reminderRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reminderService.deleteReminder(1L, userLogin))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Reminder does not exist");

            verify(reminderRepository).findById(1L);
            verifyNoInteractions(reminderMapper, reminderSchedulerService);
        }
    }

    @Nested
    class FindAllTests {
        private LocalDateTime to;
        private LocalDateTime from;
        private Pageable pageable;
        private Page<Reminder> reminderPage;
        private List<Reminder> reminderList;
        private List<ReminderDto> dtoList;

        @BeforeEach
        void setUp() {
            to = LocalDateTime.now().plusDays(5L);
            from = LocalDateTime.now().minusDays(5L);
            pageable = PageRequest.of(0, 10, Sort.by("remind"));

            reminderList = new ArrayList<>();
            reminderList.add(reminder);
            reminderList.add(createTestReminder(
                    2L, "second title", "second description", futureTime, user));
            reminderList.add(createTestReminder(
                    3L, "third title", "third description", futureTime, user));

            reminderPage = new PageImpl<>(reminderList, pageable, reminderList.size());

            dtoList = new ArrayList<>();
            dtoList.add(reminderDto);
            dtoList.add(new ReminderDto(
                    2L, 1L, "second title", "second description", futureTime));
            dtoList.add(new ReminderDto(
                    3L, 1L, "third title", "third description", futureTime));

        }

        @Test
        void findAll_allDataPresent_Success() {
            when(userRepository.findByEmail(userLogin)).thenReturn(Optional.of(user));
            when(reminderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(reminderPage);
            when(reminderMapper.toDto(reminderList.get(0))).thenReturn(dtoList.get(0));
            when(reminderMapper.toDto(reminderList.get(1))).thenReturn(dtoList.get(1));
            when(reminderMapper.toDto(reminderList.get(2))).thenReturn(dtoList.get(2));

            Page<ReminderDto> result = reminderService.findAll(userLogin, "search", to, from, pageable);
            assertThat(result.getContent().size()).isEqualTo(3);
            Assertions.assertEquals("Title", result.getContent().get(0).title());

            verify(userRepository).findByEmail(userLogin);
            verify(reminderRepository).findAll(any(Specification.class), eq(pageable));
        }
    }
}
