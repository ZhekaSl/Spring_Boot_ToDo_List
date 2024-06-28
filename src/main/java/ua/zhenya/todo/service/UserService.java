package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.UserCreateDTO;
import ua.zhenya.todo.dto.UserReadDTO;
import ua.zhenya.todo.dto.UserUpdateDTO;
import ua.zhenya.todo.mappers.UserCreateMapper;
import ua.zhenya.todo.mappers.UserReadMapper;
import ua.zhenya.todo.mappers.UserUpdateMapper;
import ua.zhenya.todo.model.Role;
import ua.zhenya.todo.repository.RoleRepository;
import ua.zhenya.todo.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserReadMapper userReadMapper;
    private final UserCreateMapper userCreateMapper;
    private final UserUpdateMapper userUpdateMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public List<UserReadDTO> findAll() {
        return userRepository.findAll()
                .stream().map(userReadMapper::map)
                .toList();
    }

    @Transactional
    public UserReadDTO create(UserCreateDTO user) {
        if (!user.getPassword().equals(user.getConfirmPassword()))
            throw new IllegalArgumentException("Passwords do not match");

        return Optional.of(user)
                .map(userCreateMapper::map)
                .map(u -> {
                    Role userRole = roleRepository.findByName("ROLE_USER");
                    u.getRoles().add(userRole);
                    return userRepository.save(u);
                })
                .map(userReadMapper::map)
                .orElseThrow();
    }

    public Optional<UserReadDTO> findById(Integer id) {
        return userRepository.findById(id)
                .map(userReadMapper::map);
    }

    @Transactional
    public Optional<UserReadDTO> update(Integer id, UserUpdateDTO userDTO) {
        return userRepository.findById(id)
                .map(foundUser -> {
                    if (!passwordEncoder.matches(userDTO.getOldPassword(), foundUser.getPassword())) {
                        throw new IllegalArgumentException("Неверный старый пароль!");
                    }
                    userUpdateMapper.map(userDTO, foundUser);
                    return foundUser;
                })
                .map(userRepository::saveAndFlush)
                .map(userReadMapper::map);
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
