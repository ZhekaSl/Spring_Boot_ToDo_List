package ua.zhenya.todo.project;

import jakarta.persistence.MappedSuperclass;
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public abstract class BaseProject {

    @Column(nullable = false)
    private String name;

    private String color;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
}