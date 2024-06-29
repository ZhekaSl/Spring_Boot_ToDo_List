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
import ua.zhenya.todo.service.TaskService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public PageResponse<TaskReadDTO> findAll(Pageable pageable) {
        Page<TaskReadDTO> page = taskService.findAll(pageable);
        return PageResponse.of(page);

    }

    @GetMapping("/users/{userId}/tasks")
    public PageResponse<TaskReadDTO> findAllByUserId(@PathVariable Integer userId, Pageable pageable) {
        Page<TaskReadDTO> page = taskService.findAllByUserId(userId, pageable);
        return PageResponse.of(page);
    }

    @GetMapping("/{id}")
    public TaskReadDTO findById(@PathVariable Integer id) {
        return taskService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public TaskReadDTO create(@RequestBody TaskCreateDTO taskCreateDTO) {
       return taskService.create(taskCreateDTO);
    }



}
