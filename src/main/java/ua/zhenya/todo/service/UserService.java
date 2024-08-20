package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.user.RegistrationUserRequest;
import ua.zhenya.todo.dto.user.UserUpdateRequest;
import ua.zhenya.todo.events.event.UserRegisteredEvent;
import ua.zhenya.todo.mappers.UserMapper;
import ua.zhenya.todo.model.Role;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.repository.UserRepository;
import ua.zhenya.todo.security.JwtResponse;
import ua.zhenya.todo.security.JwtUserDetails;

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

/*    @Cacheable(value = "users", key = "#id")*/
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id: " + id + " не найден!"));
    }

    @Transactional
/*    @Caching(put = {
            @CachePut(value = "users", key = "#result.id"),
            @CachePut(value = "users", key = "#result.email")
    })*/
    public User create(RegistrationUserRequest userDTO) {
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword()))
            throw new IllegalArgumentException("Пароли не совпадают!");

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            throw new IllegalArgumentException("Пользователь с таким email уже существует!");

        return Optional.of(userDTO)
                .map(userMapper::toEntity)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    Role userRole = roleService.getUserRole();
                    user.getRoles().add(userRole);
                    User savedUser = userRepository.save(user);
                    eventPublisher.publishEvent(new UserRegisteredEvent(this, user));
                    return savedUser;
                })
                .orElseThrow(() -> new IllegalArgumentException("Ошибка при создании пользователя!"));
    }

/*    @Cacheable(value = "users", key = "#email")*/
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с почтой: " + email + " не был найден!"));
    }

/*    @Caching(put = {
            @CachePut(value = "users", key = "#result.id"),
            @CachePut(value = "users", key = "#result.email")
    })*/
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
/*    @CacheEvict(value = "users", key = "#id")*/
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
