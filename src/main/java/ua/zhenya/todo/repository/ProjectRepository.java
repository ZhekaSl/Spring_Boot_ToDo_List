package ua.zhenya.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.project.Project;

@Repository
public interface ProjectRepository extends BaseProjectRepository<Project> {
    Page<Project> findByOwnerId(Integer ownerId, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.owner.id = :userId OR :userId IN (SELECT up.user.id FROM UserProject up WHERE up.project.id = p.id)")
    Page<Project> findAllByUserId(@Param("userId") Integer userId, Pageable pageable);
}
