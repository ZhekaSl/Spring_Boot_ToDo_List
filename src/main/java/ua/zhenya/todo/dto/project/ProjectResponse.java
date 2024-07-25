package ua.zhenya.todo.dto.project;

import jakarta.persistence.Column;
import lombok.Value;
import ua.zhenya.todo.project.ProjectPermission;

@Value
public class ProjectResponse {
    String id;
    String name;
    String color;
    Integer ownerId;
    boolean inviteUrlEnabled;
    boolean approvalRequired;
    ProjectPermission defaultPermission;


}
