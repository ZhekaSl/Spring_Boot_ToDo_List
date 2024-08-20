package ua.zhenya.todo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.zhenya.todo.project.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Entity
@Data
@EqualsAndHashCode(exclude = "inbox")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "users")
public class User implements BaseEntity<Integer>, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String password;
    private String firstname;
    private LocalDate birthDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Task> tasks = new ArrayList<>();

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Inbox inbox;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Invitation> sentInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Invitation> receivedInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<UserProject> userProjects = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    public void addTask(Task task) {
        this.tasks.add(task);
        task.setUser(this);
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
        task.setUser(null);
    }

    public void addInbox(Inbox inbox) {
        this.inbox = inbox;
        inbox.setOwner(this);
    }

    public void addProject(Project project) {
        this.projects.add(project);
        project.setOwner(this);
    }

    public void removeProject(Project project) {
        this.projects.remove(project);
        project.setOwner(null);
    }

    public void addReceivedInvitation(Invitation invitation) {
        this.receivedInvitations.add(invitation);
        invitation.setToUser(this);
    }

    public void addSentInvitation(Invitation invitation) {
        this.sentInvitations.add(invitation);
        invitation.setFromUser(this);
    }

}
