package ua.zhenya.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
}
