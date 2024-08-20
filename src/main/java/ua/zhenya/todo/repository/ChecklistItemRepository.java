package ua.zhenya.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.model.ChecklistItem;
import ua.zhenya.todo.model.Task;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Integer> {
    Page<ChecklistItem> findAllByTask(Task task, Pageable pageable);

    boolean existsByIdAndTaskId(Integer id, Integer taskId);
}
