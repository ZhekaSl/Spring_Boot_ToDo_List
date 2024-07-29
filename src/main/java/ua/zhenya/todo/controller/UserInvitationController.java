package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.invitation.InvitationResponse;
import ua.zhenya.todo.mappers.InvitationMapper;
import ua.zhenya.todo.project.Invitation;
import ua.zhenya.todo.security.JwtUserDetails;
import ua.zhenya.todo.service.InvitationService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users/me/invitations")
@RequiredArgsConstructor
public class UserInvitationController {
    private final InvitationService invitationService;
    private final InvitationMapper invitationMapper;

    @GetMapping
    public ResponseEntity<PageResponse<InvitationResponse>> findAllByUser(@AuthenticationPrincipal JwtUserDetails jwtUserDetails, Pageable pageable) {
        Page<Invitation> invitations = invitationService.findAllByToUser(jwtUserDetails.getId(), pageable);
        Page<InvitationResponse> page = invitations.map(invitationMapper::toResponse);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<Void> acceptInvitation(@AuthenticationPrincipal JwtUserDetails jwtUserDetails, @PathVariable Integer invitationId) {
        invitationService.accept(jwtUserDetails.getId(), invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{invitationId}/reject")
    public ResponseEntity<Void> rejectInvitation(@AuthenticationPrincipal JwtUserDetails jwtUserDetails, @PathVariable Integer invitationId) {
        invitationService.reject(jwtUserDetails.getId(), invitationId);
        return ResponseEntity.ok().build();
    }
}