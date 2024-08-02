package ua.zhenya.todo.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.project.*;
import ua.zhenya.todo.security.JwtUserDetails;
import ua.zhenya.todo.service.*;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomSecurityExpression {
    private final TaskService taskService;
    private final ProjectService projectService;
    private final BaseProjectService baseProjectService;
    private final InvitationService invitationService;

    public boolean canAccessUser(Integer userId) {
        Integer id = getCurrentUserId();

        return Objects.equals(userId, id);
    }

    public boolean canAccessProject(String projectId) {
        Integer id = getCurrentUserId();

        Project project = projectService.findById(projectId);
        return hasPermission(id, project, ProjectPermission.READ);
    }

    public boolean canModifyProject(String projectId) {
        Integer id = getCurrentUserId();

        BaseProject project = baseProjectService.findById(projectId);
        return hasPermission(id, project, ProjectPermission.WRITE);
    }

    public boolean isProjectOwner(String projectId) {
        Integer id = getCurrentUserId();

        BaseProject project = projectService.findById(projectId);
        return project.getOwner().getId().equals(id);
    }

    public boolean canAccessTask(Integer taskId) {
        Integer id = getCurrentUserId();

        Task task = taskService.findByIdWithDependencies(taskId);
        BaseProject baseProject = task.getProject();
        return hasPermission(id, baseProject, ProjectPermission.READ);
    }

    public boolean canModifyTask(Integer taskId) {
        Integer id = getCurrentUserId();

        Task task = taskService.findById(taskId);
        BaseProject baseProject = task.getProject();
        return hasPermission(id, baseProject, ProjectPermission.WRITE);
    }

    public boolean canChangeInvitationStatus(Integer invitationId) {
        Integer id = getCurrentUserId();

        Invitation invitation = invitationService.findById(invitationId);
        return invitation.getToUser().getId().equals(id);
    }

    private static Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        return jwtUserDetails.getId();
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

