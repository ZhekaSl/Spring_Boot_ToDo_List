package ua.zhenya.todo.events.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.zhenya.todo.events.event.UserRegisteredEvent;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.service.ProjectService;

@Component
@RequiredArgsConstructor
public class UserRegisteredListener {
    private final ProjectService projectService;
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        User user = event.getUser();
//        projectService.createInbox();

    }


}
