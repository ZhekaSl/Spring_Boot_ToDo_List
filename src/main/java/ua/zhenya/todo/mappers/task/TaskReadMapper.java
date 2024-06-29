package ua.zhenya.todo.mappers.task;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.hibernate.annotations.Parent;
import org.springframework.stereotype.Component;
import ua.zhenya.todo.dto.task.ParentTaskReadDTO;
import ua.zhenya.todo.dto.task.TaskReadDTO;
import ua.zhenya.todo.dto.user.UserReadDTO;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.mappers.user.UserReadMapper;
import ua.zhenya.todo.model.Task;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskReadMapper implements Mapper<Task, TaskReadDTO> {
    private final UserReadMapper userReadMapper;

    @Override
    public TaskReadDTO map(Task object) {
        UserReadDTO user = Optional.of(object.getUser())
                .map(userReadMapper::map)
                .orElse(null);


        ParentTaskReadDTO parentTaskReadDTO = object.getParentTask() != null ?
                new ParentTaskReadDTO(
                        object.getParentTask().getId(),
                        object.getParentTask().getName(),
                        object.getParentTask().getDescription(),
                        object.getParentTask().getTargetDate(),
                        object.getParentTask().isCompleted(),
                        object.getParentTask().getCompletedDate(),
                        object.getParentTask().getPriority()
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

