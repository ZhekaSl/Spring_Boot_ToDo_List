package ua.zhenya.todo.mappers;

import org.mapstruct.*;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.dto.task.TaskDueDetailsDTO;
import ua.zhenya.todo.dto.task.TaskResponse;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.TaskDueInfo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ChecklistItemMapper.class, SubtaskMapper.class})
public interface TaskMapper {

    @Mapping(target = "due", source = "taskDueInfo", qualifiedByName = "dueInfoToDueDto")
    @Mapping(target = "projectId", expression = "java(task.getProject().getId())")
    @Mapping(target = "creatorId", expression = "java(task.getUser().getId())")
    @Mapping(target = "parentId", expression = "java(task.getParentTask() != null ? task.getParentTask().getId() : null)")
    TaskResponse toResponse(Task task);

    @Mapping(target = "taskDueInfo", source = "due", qualifiedByName = "dueDtoToDueInfo")
    Task toEntity(TaskCreateRequest taskCreateRequest);

    @Mapping(target = "taskDueInfo", source = "due", qualifiedByName = "dueDtoToDueInfo")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void update(TaskCreateRequest taskCreateRequest, @MappingTarget Task task);

    List<TaskResponse> toResponseList(List<Task> tasks);

    @Named("dueInfoToDueDto")
    default TaskDueDetailsDTO dueDtoToDueInfo(TaskDueInfo taskDueInfo) {
        if (taskDueInfo == null) return null;

        ZoneId zoneId = ZoneId.of(taskDueInfo.getTimeZone());
        LocalDateTime dueLocalDateTime = taskDueInfo.getDueDateTime().withZoneSameInstant(zoneId).toLocalDateTime();

        return new TaskDueDetailsDTO(dueLocalDateTime, zoneId.toString());
    }

    @Named("dueDtoToDueInfo")
    default TaskDueInfo mapDueDetailsDtoToDueInfo(TaskDueDetailsDTO dto) {
        if (dto == null) {
            return null;
        }

        LocalDateTime dueDateTime = dto.getDueDateTime();
        if (dueDateTime == null) {
            return null;
        }

        ZoneId timeZone = dto.getTimeZone() != null ?
                ZoneId.of(dto.getTimeZone()) : ZoneId.of(ZoneId.systemDefault().getId());
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dueDateTime, timeZone);

        return TaskDueInfo.builder()
                .dueDateTime(zonedDateTime)
                .timeZone(timeZone.getId())
                .build();
    }
}