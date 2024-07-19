package ua.zhenya.todo.dto.invitation;

import ua.zhenya.todo.project.InvitationStatus;
import ua.zhenya.todo.project.ProjectPermission;

public class InvitationResponse {
    Integer id;
    String fromEmail;
    String toEmail;
    InvitationStatus status;
    ProjectPermission permission;
}
