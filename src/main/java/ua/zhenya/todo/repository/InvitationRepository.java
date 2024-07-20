package ua.zhenya.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.Invitation;
import ua.zhenya.todo.project.Project;

import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
    Optional<Invitation> findByProjectAndToUser(Project project, User toUser);
}
