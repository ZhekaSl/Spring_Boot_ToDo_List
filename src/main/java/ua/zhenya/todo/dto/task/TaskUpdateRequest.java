package ua.zhenya.todo.dto.task;

import lombok.Value;
import ua.zhenya.todo.model.Priority;
import java.time.LocalDate;
import java.time.LocalTime;

@Value
public class TaskUpdateRequest {
    String name;
    String description;
    LocalDate targetDate;
    LocalTime targetTime;
    Priority priority;
}
