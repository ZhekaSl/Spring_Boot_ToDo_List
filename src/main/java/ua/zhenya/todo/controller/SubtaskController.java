package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.dto.task.TaskResponse;
import ua.zhenya.todo.mappers.TaskMapper;
import ua.zhenya.todo.service.SubtaskService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks/{parentTaskId}/subtasks")
public class SubtaskController {
    private final SubtaskService subtaskService;
    private final TaskMapper taskMapper;

    @PostMapping
    public ResponseEntity<TaskResponse> create(Principal principal,
                                               @PathVariable Integer parentTaskId,
                                               @RequestBody TaskCreateRequest createDTO) {

        TaskResponse subtask = taskMapper.toResponse(subtaskService.create(principal, parentTaskId, createDTO));
        return ResponseEntity.ok(subtask);

    }

    @GetMapping
    public PageResponse<TaskResponse> findAll(Principal principal, @PathVariable Integer parentTaskId, Pageable pageable) {
        Page<TaskResponse> page = subtaskService.findAll(principal, parentTaskId, pageable)
                .map(taskMapper::toResponse);
        return PageResponse.of(page);
    }

}
