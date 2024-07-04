package ua.zhenya.todo.dto.task;

import lombok.Value;
import ua.zhenya.todo.model.Priority;

import java.time.LocalDateTime;

@Value
public class TaskResponse {
    Integer id;
    String name;
    String description;
    LocalDateTime targetDate;
    boolean completed;
    LocalDateTime completedDate;
    Priority priority;
    ParentTaskResponse parentTask;
    UserResponse user;


    @Value
    public static class ParentTaskResponse {
        Integer id;
        String name;
    }

    @Value
    public static class UserResponse {
        Integer id;
        String username;
    }

}
