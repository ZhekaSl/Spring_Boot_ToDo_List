package ua.zhenya.todo.dto.project;

import jakarta.persistence.Column;
import lombok.Value;
import ua.zhenya.todo.project.ProjectPermission;

@Value
public class ProjectResponse {
    Integer id;
    String name;
    String color;
/*    Integer ownerId;
    boolean isInbox;
    boolean inviteUrlEnabled;
    boolean approvalRequired;
    ProjectPermission defaultPermission;*/


}
