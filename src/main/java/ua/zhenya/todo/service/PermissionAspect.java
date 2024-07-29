package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.*;
import ua.zhenya.todo.repository.ChecklistItemRepository;
import ua.zhenya.todo.repository.TaskRepository;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionAspect {

    private final UserService userService;
    private final ProjectService projectService;
    private final BaseProjectService baseProjectService;
    private final TaskRepository taskRepository;

    @Before("@annotation(hasPermission) && args(username, projectId, ..) && this(projectServ)")
    public void checkProjectPermission(HasPermission hasPermission, String username, String projectId, Object projectServ) {
        User user = userService.findByEmail(username);
        Project project = projectService.findById(projectId);

        if (!hasPermission(user, project, hasPermission.value())) {
            throw new AccessDeniedException("У вас нет прав на выполнение этой операции");
        }
    }


    @Before("@annotation(ownerAccess) && args(username, projectId, ..) ")
    public void checkOwner(OwnerAccess ownerAccess, String username, String projectId) {
        User user = userService.findByEmail(username);
        Project project = projectService.findById(projectId);

        if (!isOwner(user, project)) {
            throw new AccessDeniedException("Только владелец проекта может выполнить эту операцию");
        }
    }

    @Pointcut("target(ua.zhenya.todo.service.TaskService)")
    public void isTaskService() {
    }

    @Before("isTaskService() && @annotation(hasPermission) && args(username, createRequest)")
    public void checkTaskCreatePermission(HasPermission hasPermission, String username, TaskCreateRequest createRequest) {
        User user = userService.findByEmail(username);
        BaseProject baseProject = baseProjectService.findById(createRequest.getProjectId());

        if (!hasPermission(user, baseProject, hasPermission.value())) {
            throw new AccessDeniedException("У вас нет прав на выполнение этой операции");
        }
    }

    @Before("@annotation(hasPermission) && args(username, taskId, ..)")
    public void checkTaskPermission(HasPermission hasPermission, String username, Integer taskId) {
        User user = userService.findByEmail(username);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));

        BaseProject baseProject = task.getProject();

        if (!hasPermission(user, baseProject, hasPermission.value())) {
            throw new AccessDeniedException("У вас нет прав на выполнение этой операции");
        }
    }

    private boolean hasPermission(User user, BaseProject baseProject, ProjectPermission permission) {
        // Логика проверки прав доступа
        if (baseProject.getOwner().equals(user)) {
            return true;
        }

        if (baseProject instanceof Inbox) {
            return baseProject.getOwner().equals(user);
        }

        Project project = (Project) baseProject;
        return project.getUserProjects().stream()
                .anyMatch(up -> up.getUser().equals(user) &&
                                (up.getPermission().equals(permission) || up.getPermission().equals(ProjectPermission.WRITE)));
    }

    private boolean isOwner(User user, BaseProject project) {
        return project.getOwner().equals(user);
    }

}
