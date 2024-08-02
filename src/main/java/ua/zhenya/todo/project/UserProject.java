package ua.zhenya.todo.project;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ua.zhenya.todo.model.User;

@Entity
@Table(name = "user_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Slf4j
public class UserProject {
    @EmbeddedId
    private UserProjectId id;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @ToString.Exclude
    private Project project;

    @Enumerated(EnumType.STRING)
    private ProjectPermission permission;

    public void setUser(User user) {
        this.user = user;
        user.getUserProjects().add(this);
    }

    public void setProject(Project project) {
        this.project = project;
        project.getUserProjects().add(this);
    }

    public void removeUser() {
        if (this.user != null) {
            this.user.getUserProjects().remove(this);
            this.user = null;
        }
    }

    public void removeProject() {
        if (this.project != null) {
            this.project.getUserProjects().remove(this);
            this.project = null;
        }
    }
}
