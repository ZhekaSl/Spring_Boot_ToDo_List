package ua.zhenya.todo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "checklist_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private boolean completed;
    private LocalDate targetDate;
    private LocalTime targetTime;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
