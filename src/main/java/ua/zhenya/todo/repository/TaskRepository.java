package ua.zhenya.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    Page<Task> findAllByUserIdAndParentTaskIsNull(Integer userId, Pageable pageable);


    Page<Task> findAllByUserIdAndParentTaskId(Integer user, Integer parentTaskId, Pageable pageable);
}
