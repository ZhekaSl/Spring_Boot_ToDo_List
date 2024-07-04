package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.dto.task.TaskUpdateRequest;
import ua.zhenya.todo.mappers.task.TaskCreateMapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.repository.TaskRepository;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskCreateMapper taskCreateMapper;

    public Task findById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача с id: " + id + " не найдена!"));
    }

    public Task findByIdAndVerifyOwner(Principal principal, Integer id) {
        User user = userService.findByUsername(principal.getName());
        Task task = findById(id);
        verifyTaskOwner(task, user);

        return task;
    }


    public void verifyTaskOwner(Task task, User user) {
        if (!task.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Вы не можете этого сделать!");
        }
    }


    public Page<Task> findAll(Principal principal, Pageable pageable) {
        User user = userService.findByUsername(principal.getName());

        return taskRepository.findAllByUserIdAndParentTaskIsNull(user.getId(), pageable);
    }

    @Transactional
    public Task create(Principal principal, TaskCreateRequest taskCreateRequest) {
        User user = userService.findByUsername(principal.getName());

        checkDateIfTimeIsPresent(taskCreateRequest.getTargetDate(), taskCreateRequest.getTargetTime());
        Task task = taskCreateMapper.map(taskCreateRequest);

        task.setUser(user);
        return taskRepository.save(task);
    }

    @Transactional
    public Task complete(Principal principal, Integer id) {
        User user = userService.findByUsername(principal.getName());
        Task task = findById(id);
        verifyTaskOwner(task, user);

        task.setCompleted(!task.isCompleted());

        return taskRepository.save(task);
    }

    @Transactional
    public Task update(Principal principal, Integer id, TaskUpdateRequest taskUpdateRequest) {
        User user = userService.findByUsername(principal.getName());
        Task task = findById(id);
        verifyTaskOwner(task, user);

        if (taskUpdateRequest.getName() != null && !taskUpdateRequest.getName().equals(task.getName())) {
            task.setName(taskUpdateRequest.getName());
        }
        if (taskUpdateRequest.getDescription() != null && !taskUpdateRequest.getDescription().equals(task.getDescription())) {
            task.setDescription(taskUpdateRequest.getDescription());
        }
        if (taskUpdateRequest.getPriority() != null && !taskUpdateRequest.getPriority().equals(task.getPriority())) {
            task.setPriority(taskUpdateRequest.getPriority());
        }

        if (taskUpdateRequest.getTargetDate() == null && taskUpdateRequest.getTargetTime() == null) {
            task.setTargetDate(null);
            task.setTargetTime(null);
        } else {
            if (taskUpdateRequest.getTargetDate() == null) {
                throw new IllegalArgumentException("Укажите сначала дату!");
            } else if (!taskUpdateRequest.getTargetDate().equals(task.getTargetDate())) {
                task.setTargetDate(taskUpdateRequest.getTargetDate());
            }
            if (taskUpdateRequest.getTargetTime() == null) {
                task.setTargetTime(null);
            } else if (!taskUpdateRequest.getTargetTime().equals(task.getTargetTime())) {
                task.setTargetTime(taskUpdateRequest.getTargetTime());
            }
        }
        return taskRepository.save(task);
    }


    @Transactional
    public boolean delete(Principal principal, Integer id) {
        User user = userService.findByUsername(principal.getName());

        return taskRepository.findById(id)
                .map(task -> {
                    verifyTaskOwner(task, user);
                    taskRepository.delete(task);
                    taskRepository.flush();
                    return true;
                })
                .orElseThrow(() -> new IllegalArgumentException("Задача с id: " + id + " не найдена!"));
    }

    private void checkDateIfTimeIsPresent(LocalDate date, LocalTime time) {
        if (date == null && time != null) {
            throw new IllegalArgumentException("Укажите сначала дату!");
        }
    }


}
