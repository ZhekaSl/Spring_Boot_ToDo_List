package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.Inbox;
import ua.zhenya.todo.repository.BaseProjectRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InboxService {
    private final BaseProjectRepository<Inbox> inboxRepository;
    private final UserService userService;

    @Transactional
    public void create(String username) {
        User user = userService.findByEmail(username);
        Inbox inbox = new Inbox();
        inbox.setOwner(user);
        inbox.setColor("#000000");
        inbox.setName("Inbox");

        user.addInbox(inbox);
        saveInbox(inbox);
    }

    public void saveInbox(Inbox inbox) {
        inboxRepository.save(inbox);
    }

    public String get(Integer id) {
        User user = userService.findById(id);
        return user.getInbox().getId();
    }

    @Transactional
    public String update(Integer id, String color) {
        User user = userService.findById(id);
        Inbox inbox = user.getInbox();
        inbox.setColor(color);
        saveInbox(inbox);
        return inbox.getColor();
    }
}
