package ua.zhenya.todo.dto.task;

import lombok.Value;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Value
public class TaskDueDetailsDTO {
    ZonedDateTime dueDateTime;
    boolean timeIncluded;
    ZoneId timeZone;
}
