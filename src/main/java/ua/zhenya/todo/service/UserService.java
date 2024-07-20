package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.user.RegistrationUserRequest;
import ua.zhenya.todo.dto.user.UserUpdateRequest;
import ua.zhenya.todo.events.event.UserRegisteredEvent;
import ua.zhenya.todo.mappers.UserMapper;
import ua.zhenya.todo.model.Role;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.repository.UserRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;


    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id: " + id + " не найден!"));
    }

    @Transactional
    public User create(RegistrationUserRequest userDTO) {
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword()))
            throw new IllegalArgumentException("Пароли не совпадают!");

        return Optional.of(userDTO)
                .map(userMapper::toEntity)
                .map(user -> {
                    log.debug("email {}", user.getEmail());
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    log.debug("password {}", user.getPassword());
                    Role userRole = roleService.getUserRole();
                    log.debug("role {}", userRole.getName());
                    user.getRoles().add(userRole);
                    log.debug("user roles {}", user.getRoles());
                    User savedUser = userRepository.save(user);
                    log.debug("email {}, roles {}", user.getEmail(), user.getRoles());
                    eventPublisher.publishEvent(new UserRegisteredEvent(this, user));
                    return savedUser;
                })
                .orElseThrow(() -> new IllegalArgumentException("Ошибка при создании пользователя!"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с почтой: " + email + " не был найден!"));
    }

    @Transactional
    public User update(Integer id, UserUpdateRequest userDTO) {
        return userRepository.findById(id)
                .map(foundUser -> {
                    if (!passwordEncoder.matches(userDTO.getOldPassword(), foundUser.getPassword())) {
                        throw new IllegalArgumentException("Неверный старый пароль!");
                    }
                    userMapper.update(userDTO, foundUser);
                    return foundUser;
                })
                .map(userRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id: " + id + " не найден!"));
    }

    @Transactional
    public boolean delete(Integer id) {
        return userRepository.findById(id)
                .map(entity -> {
                    userRepository.delete(entity);
                    userRepository.flush();
                    return true;
                })
                .orElse(false);
    }

}
