package ua.zhenya.todo.dto.task;

import lombok.Value;
import ua.zhenya.todo.model.Priority;

@Value
public class SubtaskResponse {
    Integer id;
    String title;
    Priority priority;
    TaskDueDetailsDTO taskDueDetailsDTO;
    boolean completed;
}
