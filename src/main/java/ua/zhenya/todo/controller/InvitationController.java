package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.invitation.InvitationCreateRequest;
import ua.zhenya.todo.dto.invitation.InvitationResponse;
import ua.zhenya.todo.mappers.InvitationMapper;
import ua.zhenya.todo.project.Invitation;
import ua.zhenya.todo.service.InvitationService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;
    private final InvitationMapper invitationMapper;


    @PostMapping
    public ResponseEntity<InvitationResponse> create(Principal principal,
                                                     @RequestBody InvitationCreateRequest invitationCreateRequest,
                                                     @PathVariable String projectId) {
        Invitation invitation = invitationService.create(principal.getName(), projectId, invitationCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(invitationMapper.toResponse(invitation));
    }

    @GetMapping
    public ResponseEntity<PageResponse<InvitationResponse>> findAllByProject(Principal principal,
                                                                             @PathVariable String projectId,
                                                                             Pageable pageable) {
        Page<InvitationResponse> page = invitationService.findAllByProject(principal.getName(), projectId, pageable)
                .map(invitationMapper::toResponse);
        return ResponseEntity.ok(PageResponse.of(page));
    }
}
