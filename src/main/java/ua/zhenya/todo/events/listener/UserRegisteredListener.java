package ua.zhenya.todo.events.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.events.event.UserRegisteredEvent;
import ua.zhenya.todo.model.MailType;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.service.InboxService;
import ua.zhenya.todo.service.mail.MailService;

import java.util.Properties;

@Component
@RequiredArgsConstructor
public class UserRegisteredListener {
    private final InboxService inboxService;
    private final MailService mailService;

    @EventListener
    @Transactional
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        User user = event.getUser();
        createInboxForUser(user);
        sendRegistrationMail(user);
    }

    private void createInboxForUser(User user) {
        inboxService.create(user.getEmail());
    }

    private void sendRegistrationMail(User user) {
        Properties properties = new Properties();
        properties.put("user", user);
        mailService.sendMail(user.getEmail(), MailType.REGISTRATION, properties);
    }

}
