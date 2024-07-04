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
import ua.zhenya.todo.dto.task.TaskUpdateRequest;
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

    @GetMapping
    public PageResponse<TaskResponse> findAll(Principal principal,
                                              Pageable pageable) {
        Page<TaskResponse> page = taskService.findAll(principal, pageable)
                .map(taskReadMapper::map);
        return PageResponse.of(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(Principal principal,
                                                 @PathVariable Integer id) {
        TaskResponse taskResponse = taskReadMapper
                .map(taskService.findByIdAndVerifyOwner(principal, id));

        return ResponseEntity.ok(taskResponse);
    }


    @PostMapping
    public ResponseEntity<TaskResponse> create(Principal principal,
                                               @Valid @RequestBody TaskCreateRequest taskCreateRequest) {
        Task task = taskService.create(principal, taskCreateRequest);
        TaskResponse taskResponse = taskReadMapper.map(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(Principal principal,
                                               @PathVariable Integer id,
                                               @Valid @RequestBody TaskUpdateRequest taskUpdateRequest) {

        Task task = taskService.update(principal, id, taskUpdateRequest);
        TaskResponse taskResponse = taskReadMapper.map(task);
        return ResponseEntity.ok(taskResponse);
    }


    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> complete(Principal principal,
                                      @PathVariable Integer id) {
        Task task = taskService.complete(principal, id);
        TaskResponse taskResponse = taskReadMapper.map(task);
        return ResponseEntity.ok(taskResponse);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal,
                                    @PathVariable Integer id) {
        return taskService.delete(principal, id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}
