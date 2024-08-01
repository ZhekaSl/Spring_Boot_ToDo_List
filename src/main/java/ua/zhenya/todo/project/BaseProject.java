package ua.zhenya.todo.project;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "base_projects")
@Data
@ToString(exclude = {"tasks"})
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseProject implements Serializable {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private String color;

    @Transient
    private User owner;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    protected abstract void generateId();

    public abstract User getOwner();

    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setProject(null);
    }

}