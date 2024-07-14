package ua.zhenya.todo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.zhenya.todo.dto.task.SubtaskResponse;
import ua.zhenya.todo.model.Task;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TaskMapper.class})

public interface SubtaskMapper {
    @Mapping(source = "name", target = "title")
    SubtaskResponse toSubtaskResponse(Task subtask);

    List<SubtaskResponse> toSubtaskResponseList(List<Task> tasks);
}
