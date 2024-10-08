package ua.zhenya.todo.dto.invitation;

import lombok.Value;
import ua.zhenya.todo.project.InvitationStatus;
import ua.zhenya.todo.project.ProjectPermission;

@Value
public class InvitationResponse {
    Integer id;
    String fromEmail;
    String toEmail;
    String projectId;
    String projectName;
    InvitationStatus status;
    ProjectPermission permission;
}
