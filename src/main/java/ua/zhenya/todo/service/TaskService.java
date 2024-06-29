package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.task.TaskCreateDTO;
import ua.zhenya.todo.dto.task.TaskReadDTO;
import ua.zhenya.todo.mappers.task.TaskCreateMapper;
import ua.zhenya.todo.mappers.task.TaskReadMapper;
import ua.zhenya.todo.repository.TaskRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskReadMapper taskReadMapper;

    private final TaskCreateMapper taskCreateMapper;

    public Page<TaskReadDTO> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskReadMapper::map);
    }

    public Optional<TaskReadDTO> findById(Integer id) {
        return taskRepository.findById(id)
                .map(taskReadMapper::map);

    }

    public Page<TaskReadDTO> findAllByUserId(Integer userId, Pageable pageable) {
        return taskRepository.findAllByUserIdAndParentTaskIsNull(userId, pageable)
                .map(taskReadMapper::map);


    }
    @Transactional
    public TaskReadDTO create(TaskCreateDTO taskDTO) {
        return Optional.of(taskDTO)
                .map(taskCreateMapper::map)
                .map(taskRepository::save)
                .map(taskReadMapper::map)
                .orElseThrow();

    }
}
