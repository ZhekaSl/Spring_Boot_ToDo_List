package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.task.TaskReadDTO;
import ua.zhenya.todo.dto.user.UserReadDTO;
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
    public ResponseEntity<TaskReadDTO> findById(@PathVariable Integer id) {
        return null;
    }

    @PostMapping
    public ResponseEntity<UserReadDTO> create(@RequestBody TaskCreateDTO taskCreateDTO) {

    }



}
