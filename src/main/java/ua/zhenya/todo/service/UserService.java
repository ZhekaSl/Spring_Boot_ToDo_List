package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.user.RegistrationUserDTO;
import ua.zhenya.todo.dto.user.UserReadDTO;
import ua.zhenya.todo.dto.user.UserUpdateDTO;
import ua.zhenya.todo.mappers.user.UserCreateMapper;
import ua.zhenya.todo.mappers.user.UserReadMapper;
import ua.zhenya.todo.mappers.user.UserUpdateMapper;
import ua.zhenya.todo.model.Role;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.repository.UserRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserCreateMapper userCreateMapper;
    private final UserUpdateMapper userUpdateMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;


    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id: " + id + " не найден!"));
    }

    @Transactional
    public User create(RegistrationUserDTO user) {
        if (!user.getPassword().equals(user.getConfirmPassword()))
            throw new IllegalArgumentException("Пароли не совпадают");

        return Optional.of(user)
                .map(userCreateMapper::map)
                .map(u -> {
                    Role userRole = roleService.getUserRole();
                    u.getRoles().add(userRole);
                    return userRepository.save(u);
                })
                .orElseThrow(() -> new IllegalArgumentException("Ошибка при создании пользователя!"));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с именем: " + username + " не был найден!"));
    }


    @Transactional
    public User update(Integer id, UserUpdateDTO userDTO) {
        return userRepository.findById(id)
                .map(foundUser -> {
                    if (!passwordEncoder.matches(userDTO.getOldPassword(), foundUser.getPassword())) {
                        throw new IllegalArgumentException("Неверный старый пароль!");
                    }
                    userUpdateMapper.map(userDTO, foundUser);
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
