package ua.zhenya.todo.events.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.events.event.UserRegisteredEvent;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.repository.ProjectRepository;
import ua.zhenya.todo.service.ProjectService;

@Component
@RequiredArgsConstructor
public class UserRegisteredListener {
    private final ProjectService projectService;
    @EventListener
    @Transactional
    public void handleCreateInboxForNewUser(UserRegisteredEvent event) {
        User user = event.getUser();
        Project inbox = projectService.createInbox(user.getUsername());

        projectService.save(inbox);
    }


}
