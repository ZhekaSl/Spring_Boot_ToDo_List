package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.project.Inbox;
import ua.zhenya.todo.security.JwtUserDetails;
import ua.zhenya.todo.service.InboxService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class InboxController {
    private final InboxService inboxService;

    @GetMapping("/inbox")
    public String getInbox(@AuthenticationPrincipal JwtUserDetails jwtUserDetails) {
        return inboxService.get(jwtUserDetails.getId());
    }

    @PutMapping("/inbox")
    public String updateInbox(@AuthenticationPrincipal JwtUserDetails jwtUserDetails, String color) {
        return inboxService.update(jwtUserDetails.getId(), color);
    }
}
