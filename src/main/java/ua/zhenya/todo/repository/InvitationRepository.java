package ua.zhenya.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.project.Invitation;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
}