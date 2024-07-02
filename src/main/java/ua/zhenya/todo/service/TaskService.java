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
        User user = userService.findByUsername(principal.getName());

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
        User user = userService.findByUsername(principal.getName());

        return taskRepository.findAllByUserIdAndParentTaskIsNull(user.getId(), pageable);
    }

    @Transactional
    public Task create(Principal principal, Task task) {
        User user = userService.findByUsername(principal.getName());
        task.setUser(user);
        return taskRepository.save(task);
    }

    @Transactional
    public boolean delete(Principal principal, Integer id) {
        User user = userService.findByUsername(principal.getName());

        return taskRepository.findById(id)
                .map(task -> {
                    if (!task.getUser().getId().equals(user.getId())) {
                        throw new UnsupportedOperationException("Вы не можете удалить эту задачу!");
                    }
                    taskRepository.delete(task);
                    taskRepository.flush();
                    return true;
                })
                .orElseThrow(() -> new IllegalArgumentException("Задача с id: " + id + " не найдена!"));
    }
}
