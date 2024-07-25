package ua.zhenya.todo.dto.user;

import lombok.Value;
import ua.zhenya.todo.project.ProjectPermission;

@Value
public class UserProjectResponse {
    Integer id;
    String firstname;
    String email;
    ProjectPermission permission;


}
