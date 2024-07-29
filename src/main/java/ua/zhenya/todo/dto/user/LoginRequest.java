package ua.zhenya.todo.dto.user;

import lombok.Value;

@Value
public class LoginRequest {
    String username;
    String password;
}
