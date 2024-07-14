package ua.zhenya.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.dto.task.TaskResponse;
import ua.zhenya.todo.mappers.TaskMapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.service.TaskService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping
    public PageResponse<TaskResponse> findAll(Principal principal,
                                              Pageable pageable) {
        Page<TaskResponse> page = taskService.findAll(principal, pageable)
                .map(taskMapper::toResponse);
        return PageResponse.of(page);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(Principal principal,
                                                 @PathVariable Integer id) {
        TaskResponse taskResponse = taskMapper
                .toResponse(taskService.findByIdAndVerifyOwner(principal, id));

        return ResponseEntity.ok(taskResponse);
    }


    @PostMapping
    public ResponseEntity<TaskResponse> create(Principal principal,
                                               @Valid @RequestBody TaskCreateRequest taskCreateRequest) {
        Task task = taskService.create(principal, taskCreateRequest);
        TaskResponse taskResponse = taskMapper.toResponse(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(Principal principal,
                                               @PathVariable Integer id,
                                               @Valid @RequestBody TaskCreateRequest taskUpdateRequest) {

        Task task = taskService.update(principal, id, taskUpdateRequest);
        TaskResponse taskResponse = taskMapper.toResponse(task);
        return ResponseEntity.ok(taskResponse);
    }


    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> complete(Principal principal,
                                      @PathVariable Integer id) {
        Task task = taskService.complete(principal, id);
        TaskResponse taskResponse = taskMapper.toResponse(task);
        return ResponseEntity.ok(taskResponse);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal,
                                    @PathVariable Integer id) {
        taskService.delete(principal, id);
        return ResponseEntity.noContent().build();
    }

}
