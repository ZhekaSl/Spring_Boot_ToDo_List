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
    Page<Task> findAllByUserIdAndParentTaskIsNull(Integer userId, Pageable pageable);

    Page<Task> findAllByParentTask(Task parentTask, Pageable pageable);

    @EntityGraph(attributePaths = {"subtasks", "checklistItems"})
    Optional<Task> findById(Integer id);

    @Query("SELECT t FROM Task t JOIN FETCH t.user WHERE t.completed = false AND " +
           "(t.taskDueInfo.dueDateTime BETWEEN :start AND :end OR t.taskDueInfo.dueDateTime = :end)")
    List<Task> findAllSoonTasks(
            @Param("start") ZonedDateTime startDate,
            @Param("end") ZonedDateTime endDate
    );
}
