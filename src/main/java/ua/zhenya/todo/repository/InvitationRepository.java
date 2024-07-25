package ua.zhenya.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.Invitation;
import ua.zhenya.todo.project.Project;

import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
    @Query("SELECT COUNT(i) > 0 FROM Invitation i WHERE i.project = :project AND i.toUser = :toUser AND i.status = ua.zhenya.todo.project.InvitationStatus.PENDING")
    boolean existsPendingInvitation(@Param("project") Project project, @Param("toUser") User toUser);

    @EntityGraph(attributePaths = {"fromUser", "toUser"})
    Page<Invitation> findAllByProject(Project project, Pageable pageable);
    @EntityGraph(attributePaths = {"project", "fromUser"})
    Page<Invitation> findAllByToUser(User user, Pageable pageable);

}
