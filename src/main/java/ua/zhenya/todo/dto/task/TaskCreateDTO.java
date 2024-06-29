package ua.zhenya.todo.dto.task;

import ua.zhenya.todo.model.Priority;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;

import java.time.LocalDateTime;

public class TaskCreateDTO {
    private String name;
    private String description;
    private LocalDateTime targetDate;
    private LocalDateTime completedDate;

    private Priority priority;

    private Task parentTask;

    private User user;
}
