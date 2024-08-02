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
}
