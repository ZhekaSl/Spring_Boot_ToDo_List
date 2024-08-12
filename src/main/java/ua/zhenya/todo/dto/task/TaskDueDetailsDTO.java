package ua.zhenya.todo.dto.task;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TaskDueDetailsDTO {
    LocalDateTime dueDateTime;
    String timeZone;
}
