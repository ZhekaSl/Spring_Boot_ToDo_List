package ua.zhenya.todo.dto.task;

import jakarta.validation.constraints.Future;
import lombok.Value;
import ua.zhenya.todo.model.Priority;

import java.time.LocalDateTime;

@Value
public class TaskUpdateDTO {
    String name;
    String description;
    @Future(message = "Введите корректную дату выполнения задачи!")
    LocalDateTime targetDate;
    boolean isCompleted;
    Priority priority;
}
