package ua.zhenya.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.dto.task.TaskResponse;
import ua.zhenya.todo.mappers.TaskMapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.security.JwtUserDetails;
import ua.zhenya.todo.service.TaskService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
public class TaskController  {
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping
    public PageResponse<TaskResponse> findAll(@AuthenticationPrincipal JwtUserDetails jwtUserDetails,
                                              Pageable pageable) {

        Page<TaskResponse> page = taskService.findAll(jwtUserDetails.getId(), pageable)
                .map(taskMapper::toResponse);
        return PageResponse.of(page);
    }

    @PreAuthorize("@customSecurityExpression.canAccessTask(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable Integer id) {
        TaskResponse taskResponse = taskMapper
                .toResponse(taskService.findById(id));
        return ResponseEntity.ok(taskResponse);
    }

    @PreAuthorize("@customSecurityExpression.canModifyProject(#taskCreateRequest.projectId)")
    @PostMapping
    public ResponseEntity<TaskResponse> create(@AuthenticationPrincipal JwtUserDetails jwtUserDetails,
                                               @Valid @RequestBody TaskCreateRequest taskCreateRequest) {
        Task task = taskService.create(jwtUserDetails.getId(), taskCreateRequest);
        TaskResponse taskResponse = taskMapper.toResponse(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @PreAuthorize("@customSecurityExpression.canModifyTask(#id)" +
                  " && @customSecurityExpression.canModifyProject(#taskUpdateRequest.projectId)")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Integer id,
                                               @Valid @RequestBody TaskCreateRequest taskUpdateRequest) {

        Task task = taskService.update(id, taskUpdateRequest);
        TaskResponse taskResponse = taskMapper.toResponse(task);
        return ResponseEntity.ok(taskResponse);
    }

    @PreAuthorize("@customSecurityExpression.canModifyTask(#id)")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Integer id) {
        Task task = taskService.complete(id);
        TaskResponse taskResponse = taskMapper.toResponse(task);
        return ResponseEntity.ok(taskResponse);
    }

    @PreAuthorize("@customSecurityExpression.canModifyTask(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
