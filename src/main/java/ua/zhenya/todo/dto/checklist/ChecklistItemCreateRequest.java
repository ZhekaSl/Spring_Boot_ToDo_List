package ua.zhenya.todo.dto.checklist;

import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;

@Value
public class ChecklistItemCreateRequest {
    String title;
    LocalDate targetDate;
    LocalTime targetTime;
}
