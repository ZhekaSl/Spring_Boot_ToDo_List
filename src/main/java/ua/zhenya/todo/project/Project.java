package ua.zhenya.todo.project;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ua.zhenya.todo.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Data
@ToString(exclude = {"owner", "userProjects", "invitations"}, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Project extends BaseProject {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProject> userProjects = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitation> invitations = new ArrayList<>();

    @Column(name = "invite_url_enabled")
    private boolean inviteUrlEnabled;

    @Enumerated(EnumType.STRING)
    private ProjectPermission defaultPermission;

    @Column(name = "approval_required")
    private boolean approvalRequired;

    @Override
    @PrePersist
    protected void generateId() {
        this.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 24));
    }

    public void addUser(UserProject userProject) {
        this.userProjects.add(userProject);
        userProject.setProject(this);
    }

    public void removeUser(UserProject userProject) {
        this.userProjects.remove(userProject);
        userProject.setProject(null);
    }
}