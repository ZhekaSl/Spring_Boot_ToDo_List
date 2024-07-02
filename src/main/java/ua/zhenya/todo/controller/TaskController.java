package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.task.TaskCreateDTO;
import ua.zhenya.todo.dto.task.TaskReadDTO;
import ua.zhenya.todo.mappers.task.TaskCreateMapper;
import ua.zhenya.todo.mappers.task.TaskReadMapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.service.TaskService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final TaskReadMapper taskReadMapper;
    private final TaskCreateMapper taskCreateMapper;

    @GetMapping
    public PageResponse<TaskReadDTO> findAll(Principal principal, Pageable pageable) {
        Page<TaskReadDTO> page = taskService.findAll(principal, pageable)
                .map(taskReadMapper::map);
        return PageResponse.of(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskReadDTO> findById(Principal principal, @PathVariable Integer id) {
        TaskReadDTO taskReadDTO = taskReadMapper
                .map(taskService.findById(principal, id));

        return ResponseEntity.ok(taskReadDTO);
    }


    @PostMapping
    public ResponseEntity<TaskReadDTO> create(Principal principal, @RequestBody TaskCreateDTO taskCreateDTO) {
        Task task = taskCreateMapper.map(taskCreateDTO);
        taskService.create(principal, task);
        return ResponseEntity.ok(taskReadMapper.map(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable Integer id) {
        return taskService.delete(principal, id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}
