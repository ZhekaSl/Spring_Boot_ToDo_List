package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.project.ProjectRequest;
import ua.zhenya.todo.dto.project.ProjectResponse;
import ua.zhenya.todo.dto.user.UserProjectResponse;
import ua.zhenya.todo.mappers.ProjectMapper;
import ua.zhenya.todo.mappers.UserProjectMapper;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.security.JwtUserDetails;
import ua.zhenya.todo.service.ProjectService;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final UserProjectMapper userProjectMapper;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@AuthenticationPrincipal JwtUserDetails jwtUserDetails, @RequestBody ProjectRequest projectRequest) {
        Project project = projectService.create(jwtUserDetails.getId(), projectRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMapper.toResponse(project));
    }

    @PreAuthorize("@customSecurityExpression.canAccessProject(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> findById(@PathVariable String id) {
        Project project = projectService.findById(id);
        return ResponseEntity.ok(projectMapper.toResponse(project));
    }


    @GetMapping
    public ResponseEntity<PageResponse<ProjectResponse>> findAll(@AuthenticationPrincipal JwtUserDetails jwtUserDetails, Pageable pageable) {
        Page<ProjectResponse> page = projectService.findAll(jwtUserDetails.getId(), pageable)
                .map(projectMapper::toResponse);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @PreAuthorize("@customSecurityExpression.isProjectOwner(#jwtUserDetails.id, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal JwtUserDetails jwtUserDetails, @PathVariable String id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@customSecurityExpression.canAccessProject(#id)")
    @GetMapping("/{id}/members")
    public ResponseEntity<PageResponse<UserProjectResponse>> findAllMembers(@PathVariable String id, Pageable pageable) {
        Page<UserProjectResponse> page = projectService.findAllMembers(id, pageable)
                .map(userProjectMapper::toResponse);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @PreAuthorize("@customSecurityExpression.isProjectOwner(#jwtUserDetails.id, #id)")
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeMember(@AuthenticationPrincipal JwtUserDetails jwtUserDetails, @PathVariable String id, @PathVariable Integer userId) {
        projectService.removeMember(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@customSecurityExpression.canModifyProject(id)")
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(@AuthenticationPrincipal JwtUserDetails jwtUserDetails,  @PathVariable String id, @RequestBody ProjectRequest projectRequest) {
        Project project = projectService.update(id, projectRequest);
        return ResponseEntity.ok(projectMapper.toResponse(project));
    }
}
