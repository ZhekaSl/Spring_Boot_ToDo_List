package ua.zhenya.todo.service;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.Inbox;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.repository.InboxRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InboxService {
    private final InboxRepository inboxRepository;
    private final UserService userService;

    @Transactional
    public Inbox createInbox(String username) {
        User user = userService.findByEmail(username);

        Inbox inbox = new Inbox();
        inbox.setOwner(user);
        inbox.setColor("#000000");
        inbox.setName("Inbox");

        return saveInbox(inbox);
    }

    public Inbox saveInbox(Inbox inbox) {
        return inboxRepository.save(inbox);
    }

}
