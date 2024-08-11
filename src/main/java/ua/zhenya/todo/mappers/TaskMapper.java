package ua.zhenya.todo.mappers;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.dto.task.TaskDueDetailsDTO;
import ua.zhenya.todo.dto.task.TaskResponse;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.TaskDueInfo;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ChecklistItemMapper.class, SubtaskMapper.class})
public interface TaskMapper {
    @Mapping(target = "projectId", expression = "java(task.getProject().getId())")
    @Mapping(target = "creatorId", expression = "java(task.getUser().getId())")
    @Mapping(target = "parentId", expression = "java(task.getParentTask() != null ? task.getParentTask().getId() : null)")
    TaskResponse toResponse(Task task);

    @Mapping(target = "taskDueInfo", source = "taskDueDetailsDTO", qualifiedByName = "mapDueDetails")
    Task toEntity(TaskCreateRequest taskCreateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "targetDate", ignore = true)
    @Mapping(target = "targetTime", ignore = true)
    void update(TaskCreateRequest taskCreateRequest, @MappingTarget Task task);

    List<TaskResponse> toResponseList(List<Task> tasks);

    @Named("mapDueDetails")
    default TaskDueInfo mapDueDetails(TaskDueDetailsDTO dto) {
        if (dto == null) {
            return null;
        }

        ZonedDateTime dueDateTime = dto.getDueDateTime();
        if (dueDateTime == null) {
            return null;
        }

        boolean timeIncluded = dto.isTimeIncluded();
        ZoneId timeZone = dto.getTimeZone() != null ? dto.getTimeZone() : ZoneId.systemDefault();

        if (!timeIncluded) {
            dueDateTime = dueDateTime.toLocalDate().atStartOfDay(timeZone);
        }

        return TaskDueInfo.builder()
                .dueDateTime(dueDateTime)
                .timeIncluded(timeIncluded)
                .timeZone(timeZone)
                .build();
    }


}