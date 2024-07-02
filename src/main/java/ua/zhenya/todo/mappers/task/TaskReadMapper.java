package ua.zhenya.todo.mappers.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.zhenya.todo.dto.task.TaskReadDTO;
import ua.zhenya.todo.dto.user.UserReadDTO;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.mappers.user.UserReadMapper;
import ua.zhenya.todo.model.Task;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskReadMapper implements Mapper<Task, TaskReadDTO> {

    @Override
    public TaskReadDTO map(Task object) {
        TaskReadDTO.UserReadDTO user = Optional.of(object.getUser())
                .map(u -> new TaskReadDTO.UserReadDTO(u.getId(), u.getUsername()))
                .orElse(null);


        TaskReadDTO.ParentTaskReadDTO parentTaskReadDTO = object.getParentTask() != null ?
                new TaskReadDTO.ParentTaskReadDTO(
                        object.getParentTask().getId(),
                        object.getParentTask().getName()
                ) : null;

        return new TaskReadDTO(
                object.getId(),
                object.getName(),
                object.getDescription(),
                object.getTargetDate(),
                object.isCompleted(),
                object.getCompletedDate(),
                object.getPriority(),
                parentTaskReadDTO,
                user);
    }
}

