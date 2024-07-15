package ua.zhenya.todo.dto.user;

import lombok.Value;

@Value
public class LoginUserRequest {
    String email;
    String password;
}
