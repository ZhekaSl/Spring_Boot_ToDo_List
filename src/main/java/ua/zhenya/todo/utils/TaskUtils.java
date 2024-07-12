package ua.zhenya.todo.utils;

import ua.zhenya.todo.model.Role;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;

import java.time.LocalDate;
import java.time.LocalTime;

public class TaskUtils {
    public static void checkDateIfTimeIsPresent(LocalDate date, LocalTime time) {
        if (date == null && time != null) {
            throw new IllegalArgumentException("Укажите сначала дату!");
        }
    }

    public static void verifyTaskOwner(Task task, User user) {
        if (!(user.isAdmin() || task.getUser().getId().equals(user.getId()))) {
            throw new IllegalArgumentException("Вы не можете этого сделать!");
        }
    }
}
