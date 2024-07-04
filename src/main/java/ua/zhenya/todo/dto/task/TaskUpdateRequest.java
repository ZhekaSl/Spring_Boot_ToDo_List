package ua.zhenya.todo.dto.task;

import jakarta.validation.constraints.Future;
import lombok.Value;
import ua.zhenya.todo.model.Priority;

import java.time.LocalDateTime;

@Value
public class TaskUpdateRequest {
    String name;
    String description;
    @Future(message = "Дата не может быть раньше текующей!")
    LocalDateTime targetDate;
    boolean isCompleted;
    Priority priority;
}
