package ua.zhenya.todo.project;

import jakarta.persistence.*;
import lombok.*;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true, exclude = "owner")
@Entity
@Table(name = "inboxes")
@Data
@ToString
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Inbox extends BaseProject {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User owner;

    @Override
    @PrePersist
    protected void generateId() {
        this.setId("inbox" + getOwner().getId());
        this.setName("Inbox");
    }
}
