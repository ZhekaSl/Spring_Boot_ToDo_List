package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.task.TaskCreateDTO;
import ua.zhenya.todo.dto.task.TaskReadDTO;
import ua.zhenya.todo.mappers.task.TaskCreateMapper;
import ua.zhenya.todo.mappers.task.TaskReadMapper;
import ua.zhenya.todo.mappers.user.UserCreateMapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.repository.TaskRepository;

import java.security.Principal;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;

    public Task findById(Principal principal, Integer id) {
        String userUsername = principal.getName();
        User user = userService.findByUsername(userUsername);

        return taskRepository.findById(id)
                .map(task -> {
                    if (!task.getUser().getId().equals(user.getId())) {
                        throw new IllegalArgumentException("Задача с id:" + id + " не принадлежит текущему пользователю!");
                    }
                    return task;
                })
                .orElseThrow(() -> new IllegalArgumentException("Задача с id:" + id + " не найдена!"));
    }


    public Page<Task> findAll(Principal principal, Pageable pageable) {
        String userUsername = principal.getName();
        User user = userService.findByUsername(userUsername);

        return taskRepository.findAllByUserIdAndParentTaskIsNull(user.getId(), pageable);
    }

    @Transactional
    public Task create(Principal principal, Task task) {
        String userUsername = principal.getName();
        User user = userService.findByUsername(userUsername);
        task.setUser(user);
        return taskRepository.save(task);
    }
}
