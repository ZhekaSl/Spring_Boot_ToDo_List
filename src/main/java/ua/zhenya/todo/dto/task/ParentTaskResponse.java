package ua.zhenya.todo.dto.task;

import lombok.Value;
import ua.zhenya.todo.model.Priority;

import java.time.LocalDateTime;

@Value
public class ParentTaskResponse {
    Integer id;
    String name;
    String description;
    LocalDateTime targetDate;
    boolean completed;
    LocalDateTime completedDate;
    Priority priority;
}
