package ua.zhenya.todo.dto.task;

import jakarta.validation.constraints.Future;
import lombok.Value;
import ua.zhenya.todo.model.Priority;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;

import java.time.LocalDateTime;

@Value
public class TaskCreateDTO {
    String name;
    String description;
    @Future
    LocalDateTime targetDate;
    Priority priority;
}
