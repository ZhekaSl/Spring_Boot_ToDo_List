package ua.zhenya.todo.project;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;

import java.util.ArrayList;
import java.util.List;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseProject {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private String color;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    protected abstract void generateId();
}