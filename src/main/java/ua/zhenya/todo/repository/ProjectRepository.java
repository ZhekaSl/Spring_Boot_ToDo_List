package ua.zhenya.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.zhenya.todo.project.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
}