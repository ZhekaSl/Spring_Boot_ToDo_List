package ua.zhenya.todo.dto.checklist;

import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Value
public class ChecklistItemCreateRequest {
    String title;
}
