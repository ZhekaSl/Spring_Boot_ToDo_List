package ua.zhenya.todo.repository;

import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.Inbox;

import java.util.Optional;

public interface InboxRepository extends BaseProjectRepository<Inbox> {
    Optional<Inbox> findByOwner(User owner);
}
