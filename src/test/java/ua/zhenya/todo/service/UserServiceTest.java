package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.zhenya.todo.dto.user.RegistrationUserRequest;
import ua.zhenya.todo.dto.user.UserUpdateRequest;
import ua.zhenya.todo.events.event.UserRegisteredEvent;
import ua.zhenya.todo.mappers.UserMapper;
import ua.zhenya.todo.model.Role;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    @Test
    void testFindAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> usersPage = new PageImpl<>(List.of(new User(), new User()));

        when(userRepository.findAll(pageable)).thenReturn(usersPage);

        Page<User> result = userService.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void testFindById_UserExists() {
        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.findById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void testFindById_UserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Пользователь с id: 1 не найден!");
    }

    @Test
    void testCreateUser_Success() {
        RegistrationUserRequest userDTO = new RegistrationUserRequest(
                "test@example.com",
                "testName",
                LocalDate.now(),
                "password",
                "password"
        );

        User user = new User();
        user.setEmail("test@example.com");

        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(roleService.getUserRole()).thenReturn(new Role((short) 1, "ROLE_USER"));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.create(userDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");

        verify(eventPublisher, times(1)).publishEvent(any(UserRegisteredEvent.class));
    }

    @Test
    void testCreateUser_PasswordMismatch() {
        RegistrationUserRequest userDTO = new RegistrationUserRequest(
                "test@example.com",
                "testName",
                LocalDate.now(),
                "password",
                "WrongPassword"
        );

        assertThatThrownBy(() -> userService.create(userDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пароли не совпадают!");
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        RegistrationUserRequest userDTO = new RegistrationUserRequest(
                "test@example.com",
                "testName",
                LocalDate.now(),
                "password",
                "password"

        );

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.create(userDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь с таким email уже существует!");
    }

    @Test
    void testFindByEmail_UserExists() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testFindByEmail_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("test@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Пользователь с почтой: test@example.com не был найден!");
    }

    @Test
    void testUpdateUser_Success() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "test@example.com",
                "testName",
                LocalDate.now(),
                "oldPassword",
                "newPassword"
        );

        User user = new User();
        user.setPassword("encodedOldPassword");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.update(1, userUpdateRequest);

        assertThat(result).isNotNull();

        verify(userMapper).update(userUpdateRequest, user);
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUser_WrongOldPassword() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "test@example.com",
                "testName",
                LocalDate.now(),
                "wrongOldPassword",
                "newPassword"
        );

        User user = new User();
        user.setPassword("encodedOldPassword");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOldPassword", "encodedOldPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.update(1, userUpdateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Неверный старый пароль!");
    }

    @Test
    void testDeleteUser_Success() {
        User user = new User();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        boolean result = userService.delete(1);

        assertThat(result).isTrue();

        verify(userRepository, times(1)).delete(user);
        verify(userRepository, times(1)).flush();
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        boolean result = userService.delete(1);

        assertThat(result).isFalse();

        verify(userRepository, never()).delete(any());
        verify(userRepository, never()).flush();
    }
}