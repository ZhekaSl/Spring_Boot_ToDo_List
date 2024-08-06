package ua.zhenya.todo.config.resolvers;

import org.springframework.stereotype.Component;
import ua.zhenya.todo.mappers.TaskMapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.service.TaskService;

/*
@Component
public class TaskQueryResolver implements GraphQLQueryResolver {
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskQueryResolver(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    public Task taskById(Integer id) {
        return taskService.findById(id);
    }
}
*/
