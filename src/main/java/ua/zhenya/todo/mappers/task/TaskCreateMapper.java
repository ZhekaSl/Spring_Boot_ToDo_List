package ua.zhenya.todo.mappers.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.repository.TaskRepository;
import ua.zhenya.todo.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class TaskCreateMapper implements Mapper<TaskCreateRequest, Task> {
    @Override
    public Task map(TaskCreateRequest object) {
       Task task = new Task();
       copy(object, task);

       return task;
    }


    @Override
    public Task map(TaskCreateRequest fromObject, Task toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    private void copy(TaskCreateRequest taskDTO, Task task) {
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setTargetDate(taskDTO.getTargetDate());
        task.setTargetTime(taskDTO.getTargetTime());
        task.setPriority(taskDTO.getPriority());
        task.setCompleted(false);
    }

}
