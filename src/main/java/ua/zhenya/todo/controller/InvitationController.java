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
import ua.zhenya.todo.dto.invitation.InvitationCreateRequest;
import ua.zhenya.todo.dto.invitation.InvitationResponse;
import ua.zhenya.todo.mappers.InvitationMapper;
import ua.zhenya.todo.project.Invitation;
import ua.zhenya.todo.security.JwtUserDetails;
import ua.zhenya.todo.service.InvitationService;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;
    private final InvitationMapper invitationMapper;

    @PreAuthorize("@customSecurityExpression.isProjectOwner(#projectId)")
    @PostMapping
    public ResponseEntity<InvitationResponse> create(@AuthenticationPrincipal JwtUserDetails jwtUserDetails,
                                                     @RequestBody InvitationCreateRequest invitationCreateRequest,
                                                     @PathVariable String projectId) {
        Invitation invitation = invitationService.create(jwtUserDetails.getId(), projectId, invitationCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(invitationMapper.toResponse(invitation));
    }

    @PreAuthorize("@customSecurityExpression.canAccessProject(#projectId)")
    @GetMapping
    public ResponseEntity<PageResponse<InvitationResponse>> findAllByProject(@PathVariable String projectId, Pageable pageable) {
        Page<InvitationResponse> page = invitationService.findAllByProject(projectId, pageable)
                .map(invitationMapper::toResponse);
        return ResponseEntity.ok(PageResponse.of(page));
    }
}
