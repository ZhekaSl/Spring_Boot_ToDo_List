package ua.zhenya.todo.utils;

import org.springframework.security.access.AccessDeniedException;
import ua.zhenya.todo.model.Role;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.BaseProject;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.project.UserProject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.stream.Stream;

public class TaskUtils {
    public static void checkDateIfTimeIsPresent(LocalDate date, LocalTime time) {
        if (date == null && time != null) {
            throw new IllegalArgumentException("Укажите сначала дату!");
        }
    }

    public static void verifyTaskOwner(Task task, User user) {
        if (!task.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Вы не можете этого сделать!");
        }
    }

    public static void verifyProjectOwner(BaseProject project, User user) {
        if (!project.getOwner().equals(user)) {
            throw new AccessDeniedException("Вы не можете этого сделать!");
        }
    }

    public static void verifyProjectMember(Project project, User user) {
        boolean isMember = project.getUserProjects().stream()
                .anyMatch(userProject -> Objects.equals(userProject.getUser(), user));
        if (!isMember) {
            throw new AccessDeniedException("Вы не можете этого сделать!");
        }
    }

    public static void verifyProjectAccess(Project project, User user) {
        if (!project.getOwner().equals(user)) {
            verifyProjectMember(project, user);
        }
    }
}
