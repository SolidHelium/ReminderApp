package com.reminderapp.reminder.service;

import com.reminderapp.reminder.dto.ChangePasswordRequest;
import com.reminderapp.reminder.dto.CreateUserRequest;
import com.reminderapp.reminder.dto.UpdateUserRequest;
import com.reminderapp.reminder.dto.UserDto;
import com.reminderapp.reminder.entity.User;
import com.reminderapp.reminder.repository.UserRepository;
import com.reminderapp.reminder.service.mapper.UserMapper;
import com.reminderapp.reminder.specification.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService tests")
class UserServiceImplTest {
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private String userLogin = "email";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setName("Name");
        user.setPassword("valid_password");
        user.setRole(UserRole.USER);
        user.setEmail("email");
        user.setTelegram("123456789");

        userDto = new UserDto(
                1L,
                "Name",
                UserRole.USER,
                "email",
                "telegram");
    }

    @Nested
    class CreateUserTests {
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "Name",
                "email",
                "UnencodedPassword",
                "telegram"
        );

        @Test
        void createUser_Success() {
            when(userRepository.existsByEmail(createUserRequest.email())).thenReturn(false);
            when(userMapper.createUser(createUserRequest)).thenReturn(user);
            when(passwordEncoder.encode(createUserRequest.password())).thenReturn("encodedPassword");
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toDto(user)).thenReturn(userDto);

            UserDto result = userService.createUser(createUserRequest);
            assertThat(result).isNotNull().isEqualTo(userDto);

            verify(userRepository).existsByEmail("email");
            verify(userMapper).createUser(createUserRequest);
            verify(passwordEncoder).encode("UnencodedPassword");
            verify(userRepository).save(user);
            verify(userMapper).toDto(user);
        }

        @Test
        void userAlreadyExists_ThrowsException() {
            when(userRepository.existsByEmail(createUserRequest.email())).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(createUserRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User with email " + createUserRequest.email() + " already exists");

            verifyNoInteractions(userMapper, passwordEncoder);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void emptyEmail_ThrowsException() {
            CreateUserRequest createRequestEmptyEmail = new CreateUserRequest(
                    "Name",
                    "",
                    "UnencodedPassword",
                    "telegram"
            );

            when(userRepository.existsByEmail(createRequestEmptyEmail.email())).thenReturn(false);

            assertThatThrownBy(() -> userService.createUser(createRequestEmptyEmail))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Email cannot be empty");

            verifyNoInteractions(userMapper, passwordEncoder);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void shortPassword_ThrowsException() {
            CreateUserRequest createRequestInvalidPassword = new CreateUserRequest(
                    "Name",
                    "email",
                    "short",
                    "telegram"
            );

            when(userRepository.existsByEmail(createRequestInvalidPassword.email())).thenReturn(false);

            assertThatThrownBy(() -> userService.createUser(createRequestInvalidPassword))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Password must be at least 8 characters long");

            verifyNoInteractions(userMapper, passwordEncoder);
            verifyNoMoreInteractions(userRepository);
        }
    }

    @Nested
    class UpdateUserTests {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "new name",
                "new email",
                "new telegram");

        UserDto updatedUserDto = new UserDto(
                1L,
                "new name",
                UserRole.USER,
                "new email",
                "new telegram");

        @Test
        void userUpdated_Success() {
            User updatedUser = new User();
            updatedUser.setUserId(1L);
            updatedUser.setName("new name");
            updatedUser.setEmail("new email");
            updatedUser.setRole(UserRole.USER);
            updatedUser.setPassword("valid_password");
            updatedUser.setTelegram("new telegram");

            when(userRepository.findByEmail(userLogin)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(updatedUser);
            when(userMapper.toDto(updatedUser)).thenReturn(updatedUserDto);

            UserDto result = userService.updateUser(updateUserRequest, userLogin);
            assertThat(result).isNotNull().isEqualTo(updatedUserDto);

            verify(userRepository).findByEmail(userLogin);
            verify(userMapper).updateUser(updateUserRequest, user);
            verify(userRepository).save(user);
            verify(userMapper).toDto(updatedUser);
        }

        @Test
        void userNotFound_ExceptionThrown() {
            when(userRepository.findByEmail(userLogin)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(updateUserRequest, userLogin))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User with login " + userLogin + " not found");

            verify(userRepository).findByEmail(userLogin);
            verifyNoInteractions(userMapper);
            verifyNoMoreInteractions(userRepository);
        }
    }

    @Nested
    class GetUserByLoginTests {
        @Test
        void getByLogin_success() {
            when(userRepository.findByEmail(userLogin)).thenReturn(Optional.of(user));
            when(userMapper.toDto(user)).thenReturn(userDto);

            UserDto result = userService.getUserByLogin(userLogin);
            assertThat(result).isNotNull().isEqualTo(userDto);

            verify(userRepository).findByEmail(userLogin);
            verify(userMapper).toDto(user);
        }

        @Test
        void getByLogin_UserNotFound_ExceptionThrown() {
            when(userRepository.findByEmail(userLogin)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserByLogin(userLogin))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User with login " + userLogin + " not found");

            verify(userRepository).findByEmail(userLogin);
            verifyNoInteractions(userMapper);
        }
    }

    @Nested
    class deleteUserTests {
        @Test
        void deleteUser_success() {
            when(userRepository.findByEmail(userLogin)).thenReturn(Optional.of(user));

            userService.deleteUser(userLogin);

            verify(userRepository).findByEmail(userLogin);
            verify(userRepository).deleteById(user.getUserId());
        }

        @Test
        void deleteUser_userNotFound_ExceptionThrown() {
            when(userRepository.findByEmail(userLogin)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser(userLogin))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User already doesn't exist");

            verify(userRepository).findByEmail(userLogin);
            verifyNoInteractions(userMapper);
        }
    }

    @Nested
    class changePasswordTests {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "valid_password",
                "new_password");

        @Test
        void changePassword_Success() {
            when(userRepository.findByEmail(userLogin)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())).thenReturn(true);
            when(passwordEncoder.encode(changePasswordRequest.newPassword())).thenReturn("valid_password");

            userService.changePassword(userLogin, changePasswordRequest);

            verify(userRepository).findByEmail(userLogin);
            verify(passwordEncoder).matches(changePasswordRequest.oldPassword(), user.getPassword());
            verify(passwordEncoder).encode(changePasswordRequest.newPassword());
            verify(userRepository).save(user);
        }

        @Test
        void changePassword_noUserFound_ExceptionThrown() {
            when(userRepository.findByEmail(userLogin)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.changePassword(userLogin, changePasswordRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User with login " + userLogin + " not found");

            verifyNoInteractions(passwordEncoder);
            verifyNoMoreInteractions(userRepository);

        }

        @Test
        void changePassword_PasswordDoesNotMatch_ExceptionThrown() {
            when(userRepository.findByEmail(userLogin)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> userService.changePassword(userLogin, changePasswordRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Password does not match");

            verify(userRepository).findByEmail(userLogin);
            verify(passwordEncoder).matches(changePasswordRequest.oldPassword(), user.getPassword());
            verifyNoMoreInteractions(userRepository, passwordEncoder);
        }
    }
}
