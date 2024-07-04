package ua.zhenya.todo.dto.task;

import lombok.Value;
import ua.zhenya.todo.model.Priority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Value
public class TaskResponse {
    Integer id;
    String name;
    String description;
    LocalDate targetDate;
    LocalTime targetTime;
    boolean completed;
    LocalDateTime completedDateTime;
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
