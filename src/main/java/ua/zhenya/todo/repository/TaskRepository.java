package ua.zhenya.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.model.Task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("select t from Task t left join fetch t.subtasks where t.parentTask is null and t.user.id = :userId")
    Page<Task> findAllParentTasksWithSubtasks(Integer userId, Pageable pageable);

    @Query("select t from Task t left join fetch t.checklistItems where t.parentTask is null and t.user.id = :userId")
    Page<Task> findAllParentTasksWithChecklistItems(Integer userId, Pageable pageable);

    @EntityGraph(attributePaths = {"project", "subtasks", "checklistItems"})
    Optional<Task> findById(Integer id);

    @Query("SELECT t FROM Task t JOIN FETCH t.user WHERE t.completed = false AND " +
           "(t.taskDueInfo.dueDateTime BETWEEN :start AND :end OR t.taskDueInfo.dueDateTime = :end)")
    List<Task> findAllSoonTasks(
            @Param("start") ZonedDateTime startDate,
            @Param("end") ZonedDateTime endDate
    );
}
