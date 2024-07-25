package ua.zhenya.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.project.UserProject;
import ua.zhenya.todo.project.UserProjectId;

import java.util.Optional;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {
    @EntityGraph(attributePaths = "user")
    Page<UserProject> findAllByProject(Project project, Pageable pageable);

    Optional<UserProject> findByProjectAndUser(Project project, User user);
}