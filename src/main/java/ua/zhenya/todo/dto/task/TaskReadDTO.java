package ua.zhenya.todo.dto.task;

import lombok.Value;
import ua.zhenya.todo.dto.user.UserReadDTO;
import ua.zhenya.todo.model.Priority;

import java.time.LocalDateTime;

@Value
public class TaskReadDTO {
    Integer id;
    String name;
    String description;
    LocalDateTime targetDate;
    boolean completed;
    LocalDateTime completedDate;
    Priority priority;
    TaskReadDTO parentTask;
    UserReadDTO user;
}
