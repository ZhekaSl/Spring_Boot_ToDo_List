package ua.zhenya.todo.project;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ua.zhenya.todo.model.User;

@Entity
@Table(name = "invitations")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    @ToString.Exclude
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    @ToString.Exclude
    private User toUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @ToString.Exclude
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false)
    private ProjectPermission permission;

}