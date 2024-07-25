package ua.zhenya.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.BaseProject;
import ua.zhenya.todo.project.Project;

import java.util.List;
import java.util.Optional;

public interface BaseProjectRepository<T extends BaseProject> extends JpaRepository<T, String> {

}