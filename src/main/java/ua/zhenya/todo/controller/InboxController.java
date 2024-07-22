package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.zhenya.todo.service.InboxService;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class InboxController {
    private final InboxService inboxService;
}
