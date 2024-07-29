package ua.zhenya.todo.events.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.events.event.UserRegisteredEvent;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.Inbox;
import ua.zhenya.todo.service.InboxService;

@Component
@RequiredArgsConstructor
public class UserRegisteredListener {
    private final InboxService inboxService;
    @EventListener
    @Transactional
    public void handleCreateInboxForNewUser(UserRegisteredEvent event) {
        User user = event.getUser();
        inboxService.create(user.getEmail());
    }

}
