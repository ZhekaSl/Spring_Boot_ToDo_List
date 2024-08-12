package ua.zhenya.todo.dto.task;

import lombok.Value;
import ua.zhenya.todo.dto.checklist.ChecklistItemResponse;
import ua.zhenya.todo.model.Priority;

import java.time.ZonedDateTime;
import java.util.List;

@Value
public class TaskResponse {
    Integer id;
    String name;
    String description;
    TaskDueDetailsDTO due;
    boolean completed;
    ZonedDateTime completedDateTime;
    Priority priority;
    Integer parentId;
    String projectId;
    Integer creatorId;
    List<ChecklistItemResponse> checklistItems;
    List<SubtaskResponse> subtasks;
}
