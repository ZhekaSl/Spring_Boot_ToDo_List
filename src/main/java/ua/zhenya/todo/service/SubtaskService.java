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
import ua.zhenya.todo.project.BaseProject;
import ua.zhenya.todo.repository.TaskRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubtaskService {
    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final BaseProjectService baseProjectService;

    @Transactional
    public Task create(Integer parentTaskId, TaskCreateRequest createDTO) {
        Task parentTask = taskService.findById(parentTaskId);
        BaseProject baseProject = baseProjectService.findById(createDTO.getProjectId());
        User user = parentTask.getUser();
        Task subtask = taskMapper.toEntity(createDTO);

        user.addTask(subtask);
        parentTask.addSubtask(subtask);
        baseProject.addTask(subtask);
        return taskRepository.save(subtask);
    }

    public Page<Task> findAll(Integer parentTaskId, Pageable pageable) {
        Task parentTask = taskService.findById(parentTaskId);
        return taskRepository.findAllByParentTask(parentTask, pageable);
    }
}
