package ua.zhenya.todo.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.BaseProject;
import ua.zhenya.todo.project.Inbox;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.project.ProjectPermission;
import ua.zhenya.todo.security.JwtUserDetails;
import ua.zhenya.todo.service.BaseProjectService;
import ua.zhenya.todo.service.ProjectService;
import ua.zhenya.todo.service.TaskService;
import ua.zhenya.todo.service.UserService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomSecurityExpression {
    private final UserService userService;
    private final TaskService taskService;
    private final ProjectService projectService;
    private final BaseProjectService baseProjectService;

    public boolean canAccessUser(Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        Integer id = jwtUserDetails.getId();

        return Objects.equals(userId, id);
    }

    public boolean canAccessProject(String projectId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        Integer id = jwtUserDetails.getId();

        Project project = projectService.findById(projectId);
        return hasPermission(id, project, ProjectPermission.READ);
    }

    public boolean canModifyProject(String projectId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        Integer id = jwtUserDetails.getId();

        Project project = projectService.findById(projectId);
        return hasPermission(id, project, ProjectPermission.WRITE);
    }

    public boolean isProjectOwner(Integer id, String projectId) {
        User user = userService.findById(id);
        BaseProject project = projectService.findById(projectId);
        return project.getOwner().equals(user);
    }

    public boolean canAccessTask(Integer taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        Integer id = jwtUserDetails.getId();

        Task task = taskService.findById(taskId);
        BaseProject baseProject = task.getProject();
        return hasPermission(id, baseProject, ProjectPermission.READ);
    }

    public boolean canModifyTask(Integer taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        Integer id = jwtUserDetails.getId();
        Task task = taskService.findById(taskId);

        BaseProject baseProject = baseProjectService.findById(task.getProject().getId());
        return hasPermission(id, baseProject, ProjectPermission.WRITE);
    }

    private boolean hasPermission(Integer userId, BaseProject baseProject, ProjectPermission permission) {
        if (baseProject.getOwner().getId().equals(userId)) {
            return true;
        }
        if (baseProject instanceof Inbox) {
            return baseProject.getOwner().getId().equals(userId);
        }
        Project project = (Project) baseProject;
        return project.getUserProjects().stream()
                .anyMatch(up -> up.getUser().getId().equals(userId) &&
                                (up.getPermission().equals(permission) || up.getPermission().equals(ProjectPermission.WRITE)));
    }
}

