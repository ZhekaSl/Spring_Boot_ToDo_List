package ua.zhenya.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.model.Task;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    Page<Task> findAllByUserIdAndParentTaskIsNull(Integer userId, Pageable pageable);

    Page<Task> findAllByParentTask(Task parentTask, Pageable pageable);

    @EntityGraph(attributePaths = {"subtasks", "checklistItems"})
    Optional<Task> findById(Integer id);
}
