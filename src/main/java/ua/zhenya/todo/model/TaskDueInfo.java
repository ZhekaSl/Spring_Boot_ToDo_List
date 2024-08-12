package ua.zhenya.todo.model;

import jakarta.persistence.Column;
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
    @Column(name = "due_timestamp")
    private ZonedDateTime dueDateTime;
    @Column(name = "time_zone")
    private String timeZone;
}