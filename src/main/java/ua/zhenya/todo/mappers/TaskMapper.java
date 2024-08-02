package ua.zhenya.todo.mappers;

import org.mapstruct.*;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.dto.task.TaskResponse;
import ua.zhenya.todo.model.Task;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ChecklistItemMapper.class, SubtaskMapper.class})
public interface TaskMapper {
    @Mapping(target = "projectId", expression = "java(task.getProject().getId())")
    @Mapping(target = "creatorId", expression = "java(task.getUser().getId())")
    @Mapping(target = "parentId", expression = "java(task.getParentTask() != null ? task.getParentTask().getId() : null)")
    TaskResponse toResponse(Task task);

    Task toEntity(TaskCreateRequest taskCreateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "targetDate", ignore = true)
    @Mapping(target = "targetTime", ignore = true)
    void update(TaskCreateRequest taskCreateRequest, @MappingTarget Task task);

    List<TaskResponse> toResponseList(List<Task> tasks);


}