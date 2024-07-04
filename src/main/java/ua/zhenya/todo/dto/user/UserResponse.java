package ua.zhenya.todo.dto.user;

import lombok.Value;

import java.time.LocalDate;

@Value
public class UserResponse {
    Integer id;
    String username;
    String firstname;
    LocalDate birthDate;
}
