package ua.zhenya.todo.mappers.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.zhenya.todo.dto.task.TaskResponse;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.model.Task;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskReadMapper implements Mapper<Task, TaskResponse> {

    @Override
    public TaskResponse map(Task object) {
        TaskResponse.UserResponse user = Optional.of(object.getUser())
                .map(u -> new TaskResponse.UserResponse(u.getId(), u.getUsername()))
                .orElse(null);


        TaskResponse.ParentTaskResponse parentTaskResponse = object.getParentTask() != null ?
                new TaskResponse.ParentTaskResponse(
                        object.getParentTask().getId(),
                        object.getParentTask().getName()
                ) : null;

        return new TaskResponse(
                object.getId(),
                object.getName(),
                object.getDescription(),
                object.getTargetDate(),
                object.isCompleted(),
                object.getCompletedDate(),
                object.getPriority(),
                parentTaskResponse,
                user);
    }
}

