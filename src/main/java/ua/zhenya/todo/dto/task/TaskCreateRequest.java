package ua.zhenya.todo.dto.task;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Value;
import ua.zhenya.todo.model.Priority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Value
public class TaskCreateRequest {
    String name;
    String description;
    LocalDate targetDate;
    LocalTime targetTime;
    Priority priority;
    String projectId;
}
