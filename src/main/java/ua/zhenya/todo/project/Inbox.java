package ua.zhenya.todo.project;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inboxes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PrimaryKeyJoinColumn(name = "id")
public class Inbox extends BaseProject {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;


    @Override
    @PrePersist
    protected void generateId() {
        this.setId("inbox" + owner.getId());
    }
}
