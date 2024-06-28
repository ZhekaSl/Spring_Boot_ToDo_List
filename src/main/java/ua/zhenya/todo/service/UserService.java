package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.zhenya.todo.dto.UserCreateDto;
import ua.zhenya.todo.dto.UserReadDTO;
import ua.zhenya.todo.mappers.UserCreateMapper;
import ua.zhenya.todo.mappers.UserReadMapper;
import ua.zhenya.todo.model.Role;
import ua.zhenya.todo.model.User;
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
    private final RoleRepository roleRepository;


    public List<UserReadDTO> findAll() {
        return userRepository.findAll()
                .stream().map(userReadMapper::map)
                .toList();
    }

    @Transactional
    public UserReadDTO create(UserCreateDto user) {
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

    public User update(Integer id, UserCreateDto user) {
        return null;
    }
}
