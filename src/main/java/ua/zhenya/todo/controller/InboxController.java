package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.project.Inbox;
import ua.zhenya.todo.service.InboxService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class InboxController {
    private final InboxService inboxService;

    @GetMapping("/inbox")
    public String getInbox(Principal principal) {
        return inboxService.get(principal.getName());
    }

    @PutMapping("/inbox")
    public String updateInbox(Principal principal, String color) {
        return inboxService.update(principal.getName(), color);
    }
}
