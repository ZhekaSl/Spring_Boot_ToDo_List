package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.mappers.TaskMapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.TaskDueInfo;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.BaseProject;
import ua.zhenya.todo.repository.TaskRepository;

import java.time.*;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final BaseProjectService baseProjectService;
    private final TaskMapper taskMapper;


    @Cacheable(value = "tasks", key = "#id")
    public Task findByIdCached(Integer id) {
        return findById(id);
    }

    public Task findById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача с id: " + id + " не найдена!"));
    }

    public Page<Task> findAll(Integer userId, Pageable pageable) {
        return taskRepository.findAllByUserIdAndParentTaskIsNull(userId, pageable);
    }

    @Transactional
    public Task create(Integer userId, TaskCreateRequest taskCreateRequest) {
        User user = userService.findById(userId);
        BaseProject baseProject = baseProjectService.findById(taskCreateRequest.getProjectId());
        Hibernate.initialize(user.getTasks());

        Task task = taskMapper.toEntity(taskCreateRequest);

        TaskDueInfo taskDueInfo = task.getTaskDueInfo();
        if (taskDueInfo != null) {
            ZonedDateTime dueDateTime = taskDueInfo.getDueDateTime();
            if (dueDateTime != null) {
                taskDueInfo.setDueDateTime(convertZonedDateToUTC(dueDateTime));
            }
        }

        baseProject.addTask(task);
        user.addTask(task);
        return taskRepository.save(task);
    }

    @Transactional
    @CachePut(value = "tasks", key = "#id")
    public Task complete(Integer id) {
        Task task = findById(id);

        task.setCompleted(!task.isCompleted());

        if (task.isCompleted()) {
            ZoneId zoneId = task.getTaskDueInfo().getTimeZone() != null
                    ? ZoneId.of(task.getTaskDueInfo().getTimeZone())
                    : ZoneId.systemDefault();
            task.setCompletedDateTime(ZonedDateTime.now(zoneId));
        } else {
            task.setCompletedDateTime(null);
        }

        return taskRepository.save(task);
    }


    @Transactional
    @CachePut(value = "tasks", key = "#id")
    public Task update(Integer id, TaskCreateRequest taskUpdateRequest) {
        Task task = findById(id);

        taskMapper.update(taskUpdateRequest, task);

        if (taskUpdateRequest.getProjectId() != null &&
            !taskUpdateRequest.getProjectId().equals(task.getProject().getId())) {
            BaseProject newProject = baseProjectService.findById(taskUpdateRequest.getProjectId());
            BaseProject oldProject = task.getProject();

            if (task.getParentTask() != null) {
                task.getParentTask().removeSubtask(task);
            }
            oldProject.removeTask(task);
            newProject.addTask(task);
        }

        TaskDueInfo taskDueInfo = task.getTaskDueInfo();
        if (taskDueInfo != null) {
            ZonedDateTime dueDateTime = taskDueInfo.getDueDateTime();
            if (dueDateTime != null) {
                taskDueInfo.setDueDateTime(convertZonedDateToUTC(dueDateTime));
            }
        }

        return taskRepository.save(task);
    }

    @Transactional
    @CacheEvict(value = "tasks", key = "#id")
    public void delete(Integer id) {
        Task task = findById(id);
        Task parentTask = task.getParentTask();
        if (parentTask != null) {
            parentTask.removeSubtask(task);
        }
        task.getUser().removeTask(task);
        task.getProject().removeTask(task);

        taskRepository.delete(task);
    }

    public List<Task> findAllSoonTasks(Duration duration) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime deadline = now.plus(duration);

        return taskRepository.findAllSoonTasks(now, deadline);
    }

    private ZonedDateTime convertZonedDateToUTC(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
    }
}
