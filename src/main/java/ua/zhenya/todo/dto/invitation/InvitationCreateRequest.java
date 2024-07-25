package ua.zhenya.todo.dto.invitation;

import lombok.Value;
import ua.zhenya.todo.project.ProjectPermission;

@Value
public class InvitationCreateRequest {
    String toEmail;
    ProjectPermission permission;
}
