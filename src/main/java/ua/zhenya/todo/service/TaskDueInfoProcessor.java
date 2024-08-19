package ua.zhenya.todo.service;

import org.springframework.stereotype.Component;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.TaskDueInfo;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class TaskDueInfoProcessor {

    public void processTaskDueInfo(Task task) {
        TaskDueInfo taskDueInfo = task.getTaskDueInfo();
        if (taskDueInfo != null) {
            ZonedDateTime dueDateTime = taskDueInfo.getDueDateTime();
            if (dueDateTime != null) {
                taskDueInfo.setDueDateTime(convertToUtc(dueDateTime));
            }
        }
    }

    public ZoneId determineZoneId(Task task) {
        TaskDueInfo taskDueInfo = task.getTaskDueInfo();
        if (taskDueInfo != null && taskDueInfo.getTimeZone() != null) {
            return ZoneId.of(taskDueInfo.getTimeZone());
        } else {
            return ZoneId.systemDefault();
        }
    }

    private ZonedDateTime convertToUtc(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
    }
}