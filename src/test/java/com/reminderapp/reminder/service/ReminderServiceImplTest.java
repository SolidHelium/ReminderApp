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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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

    private final ReminderDto reminderDto = new ReminderDto(
            1L,
            1L,
            "Title",
            "Description",
            futureTime
    );

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setName("testUser");
        user.setPassword("valid_password");
        user.setRole(UserRole.USER);
        user.setEmail("validemail@email.com");
        user.setTelegram("123456789");

        reminder = new Reminder();
        reminder.setReminderId(1L);
        reminder.setTitle("Title");
        reminder.setDescription("Description");
        reminder.setRemind(futureTime);
        reminder.setUser(user);
    }

    @Nested
    class CreateReminderTests {
        private final CreateReminderRequest createReminderRequest = new CreateReminderRequest(
                1L,
                "Title",
                "Description",
                futureTime
        );

        @Test
        void reminderCreated_validRequest() {
            when(reminderRepository.save(reminder)).thenReturn(reminder);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(reminderMapper.toEntity(createReminderRequest, user)).thenReturn(reminder);
            when(reminderMapper.toDto(reminder)).thenReturn(reminderDto);

            ReminderDto result = reminderService.createReminder(createReminderRequest);
            assertThat(result).isNotNull().isEqualTo(reminderDto);

            verify(userRepository).findById(1L);
            verify(reminderMapper).toEntity(createReminderRequest, user);
            verify(reminderRepository).save(reminder);
            verify(reminderSchedulerService).scheduleReminder(reminder);
            verify(reminderMapper).toDto(reminder);
        }

        @Test
        void userNotFound_ExceptionThrown() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reminderService.createReminder(createReminderRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User not found");

            verify(userRepository).findById(1L);
            verifyNoInteractions(reminderMapper, reminderRepository, reminderSchedulerService);
        }

        @Test
        void invalidTime_ExceptionThrown() {
            CreateReminderRequest pastRequest = new CreateReminderRequest(
                    1L,
                    "Title",
                    "Description",
                    pastTime
            );
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> reminderService.createReminder(pastRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Reminder date and time should be in the future");

            verify(userRepository).findById(1L);
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
            when(reminderRepository.findById(1L)).thenReturn(Optional.of(reminder));
            ReminderDto updatedDto = new ReminderDto(
                    1L,
                    1L,
                    "Updated title",
                    "Updated description",
                    updateTime
            );
            when(reminderRepository.save(reminder)).thenReturn(reminder);
            when(reminderMapper.toDto(reminder)).thenReturn(updatedDto);

            ReminderDto result = reminderService.updateReminder(updateReminderRequest);
            assertThat(result).isNotNull().isEqualTo(updatedDto);

            verify(reminderRepository).findById(1L);
            verify(reminderMapper).updateEntity(updatedDto, reminder);
            verify(reminderRepository).save(reminder);
            verify(reminderSchedulerService).rescheduleReminder(reminder);
            verify(reminderMapper).toDto(reminder);
        }

        @Test
        void reminderNotFound_ExceptionThrown() {
            when(reminderRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reminderService.updateReminder(updateReminderRequest))
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

            when(reminderRepository.findById(1L)).thenReturn(Optional.of(reminder));

            assertThatThrownBy(() -> reminderService.updateReminder(invalidTimeUpdateRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Reminder date and time should be in the future");

            verify(reminderRepository).findById(1L);
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

            ReminderDto result = reminderService.getReminderById(1L);
            assertThat(result).isNotNull().isEqualTo(reminderDto);

            verify(reminderRepository).findById(1L);
            verify(reminderMapper).toDto(reminder);
        }

        @Test
        void reminderNotFound_ExceptionThrown() {
            when(reminderRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reminderService.getReminderById(1L))
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

            reminderService.deleteReminder(1L);

            verify(reminderRepository).findById(1L);
            verify(reminderRepository).deleteById(1L);
            verify(reminderSchedulerService).cancelReminder(reminder);
        }

        @Test
        void reminderNotFound_ExceptionThrown() {
            when(reminderRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reminderService.getReminderById(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Reminder not found");

            verify(reminderRepository).findById(1L);
            verifyNoInteractions(reminderMapper);
        }
    }

    @Nested
    class FindAllTests {

//        @Test
//        void findAll_allDataPresent_Success() {
//            //Pageable pageable = PageRequest.of()
//            //Page<ReminderDto> result = reminderService.findAll("search", to, from);
//
//        }
    }
}
