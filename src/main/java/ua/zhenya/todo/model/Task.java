package ua.zhenya.todo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task implements BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private LocalDate targetDate;
    private LocalTime targetTime;
    private boolean completed;
    @Column(name = "completed_timestamp")
    private LocalDateTime completedDateTime;
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Task> subtasks = new ArrayList<>();
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ChecklistItem> checklistItems = new ArrayList<>();

    public void addChecklistItem(ChecklistItem checklistItem) {
        this.checklistItems.add(checklistItem);
        checklistItem.setTask(this);
    }

    public void removeChecklistItem(ChecklistItem checklistItem) {
        this.checklistItems.remove(checklistItem);
        checklistItem.setTask(null);
    }

    public void addSubtask(Task subtask) {
        this.subtasks.add(subtask);
        subtask.setParentTask(this);
    }

    public void removeSubtask(Task subtask) {
        this.subtasks.remove(subtask);
        subtask.setParentTask(null);
    }
}
