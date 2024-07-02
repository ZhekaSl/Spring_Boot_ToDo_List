package ua.zhenya.todo.dto.task;

import lombok.Value;
import ua.zhenya.todo.model.Priority;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;

import java.time.LocalDateTime;

@Value
public class TaskCreateDTO {
    String name;
    String description;
    LocalDateTime targetDate;

    Priority priority;
}
