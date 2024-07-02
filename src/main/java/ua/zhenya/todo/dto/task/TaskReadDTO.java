package ua.zhenya.todo.dto.task;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;
import ua.zhenya.todo.dto.user.UserReadDTO;
import ua.zhenya.todo.model.Priority;
import ua.zhenya.todo.model.Task;

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
    ParentTaskReadDTO parentTaskReadDTO;
    UserReadDTO userReadDTO;


    @Value
    public static class ParentTaskReadDTO {
        Integer id;
        String name;
    }

    @Value
    public static class UserReadDTO {
        Integer id;
        String username;
    }

}
