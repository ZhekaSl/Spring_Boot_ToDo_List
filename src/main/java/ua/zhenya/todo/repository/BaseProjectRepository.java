package ua.zhenya.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.zhenya.todo.project.BaseProject;

public interface BaseProjectRepository<T extends BaseProject> extends JpaRepository<T, String> {

}