package ua.zhenya.todo.project;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
public class UserProjectId implements Serializable {
    private Integer userId;
    private Integer projectId;
}
