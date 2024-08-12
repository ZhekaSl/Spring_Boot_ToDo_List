package ua.zhenya.todo.dto.task;

import lombok.Value;
import ua.zhenya.todo.model.Priority;

@Value
public class TaskCreateRequest {
    String name;
    String description;
    TaskDueDetailsDTO due;
    Priority priority;
    String projectId;
}
