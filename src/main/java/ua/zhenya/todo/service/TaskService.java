package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.mappers.TaskMapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.BaseProject;
import ua.zhenya.todo.project.ProjectPermission;
import ua.zhenya.todo.repository.TaskRepository;
import ua.zhenya.todo.utils.TaskUtils;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final BaseProjectService baseProjectService;
    private final TaskMapper taskMapper;

    public Task findById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача с id: " + id + " не найдена!"));
    }

    @HasPermission(ProjectPermission.READ)
    public Task findById(String username, Integer id) {
        return findById(id);
    }

    @HasPermission(ProjectPermission.READ)
    public Page<Task> findAll(String username, Pageable pageable) {
        User user = userService.findByEmail(username);
        return taskRepository.findAllByUserIdAndParentTaskIsNull(user.getId(), pageable);
    }

    @Transactional
    @HasPermission(ProjectPermission.WRITE)
    public Task create(String username, TaskCreateRequest taskCreateRequest) {
        User user = userService.findByEmail(username);
        BaseProject baseProject = baseProjectService.findById(taskCreateRequest.getProjectId());
/*        TaskUtils.verifyProjectOwner(baseProject, user);*/

        TaskUtils.checkDateIfTimeIsPresent(taskCreateRequest.getTargetDate(), taskCreateRequest.getTargetTime());
        Task task = taskMapper.toEntity(taskCreateRequest);
        baseProject.addTask(task);
        user.addTask(task);
        return taskRepository.save(task);
    }

    @Transactional
    @HasPermission(ProjectPermission.WRITE)
    public Task complete(String username, Integer id) {
        Task task = findById(id);

        task.setCompleted(!task.isCompleted());

        if (task.isCompleted()) {
            task.setCompletedDateTime(LocalDateTime.now());
        } else {
            task.setCompletedDateTime(null);
        }

        return taskRepository.save(task);
    }

    @HasPermission(ProjectPermission.WRITE)
    @Transactional
    public Task update(String username, Integer id, TaskCreateRequest taskUpdateRequest) {
        Task task = findById(id);

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


    @HasPermission(ProjectPermission.WRITE)
    @Transactional
    public void delete(String username, Integer id) {
        User user = userService.findByEmail(username);
        Task task = findById(id);
        Task parentTask = task.getParentTask();
        if (parentTask != null) {
            parentTask.removeSubtask(task);
        }
        user.removeTask(task);

        taskRepository.delete(task);
    }
}
