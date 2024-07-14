package ua.zhenya.todo.dto.task;

import lombok.Value;
import ua.zhenya.todo.model.Priority;

import java.time.LocalDate;
import java.time.LocalTime;

@Value
public class SubtaskResponse {
    Integer id;
    String title;
    Priority priority;
    LocalDate targetDate;
    LocalTime targetTime;
    boolean completed;
}
