package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.user.UserCreateDTO;
import ua.zhenya.todo.dto.user.UserReadDTO;
import ua.zhenya.todo.dto.user.UserUpdateDTO;
import ua.zhenya.todo.mappers.user.UserCreateMapper;
import ua.zhenya.todo.mappers.user.UserReadMapper;
import ua.zhenya.todo.mappers.user.UserUpdateMapper;
import ua.zhenya.todo.model.Role;
import ua.zhenya.todo.repository.RoleRepository;
import ua.zhenya.todo.repository.UserRepository;

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


    public Page<UserReadDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userReadMapper::map);
    }

    @Transactional
    public UserReadDTO create(UserCreateDTO user) {
        if (!user.getPassword().equals(user.getConfirmPassword()))
            throw new IllegalArgumentException("Пароли не совпадают");

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
