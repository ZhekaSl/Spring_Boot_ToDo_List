package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.task.TaskReadDTO;
import ua.zhenya.todo.mappers.task.TaskReadMapper;
import ua.zhenya.todo.repository.TaskRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskReadMapper taskReadMapper;

    public Page<TaskReadDTO> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskReadMapper::map);
    }

    public Page<TaskReadDTO> findAllByUserId(Integer userId, Pageable pageable) {
        return taskRepository.findAllByUserIdAndParentTaskIsNull(userId, pageable)
                .map(taskReadMapper::map);


    }
}
