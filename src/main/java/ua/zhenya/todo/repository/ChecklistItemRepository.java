package ua.zhenya.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.model.ChecklistItem;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Integer> {
    Page<ChecklistItem> findAllByTaskId(Integer taskId, Pageable pageable);
}
