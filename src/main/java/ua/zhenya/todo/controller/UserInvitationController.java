package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.invitation.InvitationResponse;
import ua.zhenya.todo.mappers.InvitationMapper;
import ua.zhenya.todo.project.Invitation;
import ua.zhenya.todo.service.InvitationService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users/me/invitations")
@RequiredArgsConstructor
public class UserInvitationController {
    private final InvitationService invitationService;
    private final InvitationMapper invitationMapper;

    @GetMapping
    public ResponseEntity<PageResponse<InvitationResponse>> findAllByUser(Principal principal, Pageable pageable) {
        Page<Invitation> invitations = invitationService.findAllByToUser(principal.getName(), pageable);
        Page<InvitationResponse> page = invitations.map(invitationMapper::toResponse);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<Void> acceptInvitation(Principal principal, @PathVariable Integer invitationId) {
        invitationService.accept(principal.getName(), invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{invitationId}/reject")
    public ResponseEntity<Void> rejectInvitation(Principal principal, @PathVariable Integer invitationId) {
        invitationService.reject(principal.getName(), invitationId);
        return ResponseEntity.ok().build();
    }
}