package ua.zhenya.todo.dto.invitation;

import ua.zhenya.todo.project.ProjectPermission;

public class InvitationCreateRequest {
    String toEmail;
    ProjectPermission permission;
    Integer projectId;

}
