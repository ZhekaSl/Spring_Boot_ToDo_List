package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.mappers.TaskMapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.repository.TaskRepository;
import ua.zhenya.todo.utils.TaskUtils;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubtaskService {
    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskMapper taskMapper;

    @Transactional
    public Task create(Principal principal, Integer parentTaskId, TaskCreateRequest createDTO) {
        User user = userService.findByEmail(principal.getName());
        Task parentTask = taskService.findById(parentTaskId);
        TaskUtils.verifyTaskOwner(parentTask, user);

        Task subtask = taskMapper.toEntity(createDTO);

        user.addTask(subtask);
        parentTask.addSubtask(subtask);
        return taskRepository.save(subtask);

    }

    public Page<Task> findAll(Principal principal, Integer parentTaskId, Pageable pageable) {
        User user = userService.findByEmail(principal.getName());
        Task parentTask = taskService.findById(parentTaskId);
        TaskUtils.verifyTaskOwner(parentTask, user);

        return taskRepository.findAllByParentTaskId(parentTaskId, pageable);
    }
}
