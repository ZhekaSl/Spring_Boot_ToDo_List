package ua.zhenya.todo.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class TaskDueInfo {
    private ZonedDateTime dueDateTime;
    private boolean timeIncluded;
    private ZoneId timeZone;
}