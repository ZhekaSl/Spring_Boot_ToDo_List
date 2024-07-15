package ua.zhenya.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.project.UserProject;
import ua.zhenya.todo.project.UserProjectId;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {
}