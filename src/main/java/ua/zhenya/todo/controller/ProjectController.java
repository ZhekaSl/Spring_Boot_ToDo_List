package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.project.ProjectRequest;
import ua.zhenya.todo.dto.project.ProjectResponse;
import ua.zhenya.todo.dto.user.UserProjectResponse;
import ua.zhenya.todo.mappers.ProjectMapper;
import ua.zhenya.todo.mappers.UserProjectMapper;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.service.ProjectService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final UserProjectMapper userProjectMapper;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(Principal principal, @RequestBody ProjectRequest projectRequest) {
        Project project = projectService.create(principal.getName(), projectRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMapper.toResponse(project));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> findById(Principal principal, @PathVariable String id) {
        Project project = projectService.findById(principal.getName(), id);
        return ResponseEntity.ok(projectMapper.toResponse(project));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProjectResponse>> findAll(Principal principal, Pageable pageable) {
        Page<ProjectResponse> page = projectService.findAll(principal.getName(), pageable)
                .map(projectMapper::toResponse);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable String id) {
        projectService.delete(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<PageResponse<UserProjectResponse>> findAllMembers(Principal principal, @PathVariable String id, Pageable pageable) {
        Page<UserProjectResponse> page = projectService.findAllMembers(principal.getName(), id, pageable)
                .map(userProjectMapper::toResponse);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeMember(Principal principal, @PathVariable String id, @PathVariable Integer userId) {
        projectService.removeMember(principal.getName(), id, userId);
        return ResponseEntity.noContent().build();
    }

/*    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(Principal principal, @PathVariable Integer id, @RequestBody ProjectRequest projectRequest) {
        Project project = projectService.update(principal.getName(), id, projectRequest);
        return ResponseEntity.ok(projectMapper.toResponse(project));
    }*/
}
