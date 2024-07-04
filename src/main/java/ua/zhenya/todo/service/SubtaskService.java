package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.mappers.task.TaskCreateMapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.repository.TaskRepository;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubtaskService {
    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskCreateMapper taskCreateMapper;

    @Transactional
    public Task create(Principal principal, Integer parentTaskId, TaskCreateRequest createDTO) {
        User user = userService.findByUsername(principal.getName());
        Task parentTask = taskService.findById(parentTaskId);
        taskService.verifyTaskOwner(parentTask, user);

        return Optional.of(createDTO)
                .map(taskCreateMapper::map)
                .map(subtask -> {
                    subtask.setParentTask(parentTask);
                    subtask.setUser(user);
                    return subtask;
                })
                .map(taskRepository::save)
                .orElseThrow();
    }

    public Page<Task> findAll(Principal principal, Integer parentTaskId, Pageable pageable) {
        User user = userService.findByUsername(principal.getName());
        Task parentTask = taskService.findById(parentTaskId);
        taskService.verifyTaskOwner(parentTask, user);

        return taskRepository.findAllByParentTaskId(parentTaskId, pageable);
    }


}
