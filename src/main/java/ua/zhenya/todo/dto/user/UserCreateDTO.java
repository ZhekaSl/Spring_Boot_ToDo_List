package ua.zhenya.todo.dto.user;

import lombok.Value;

import java.time.LocalDate;

@Value
public class UserCreateDTO {
    String username;
    String firstName;
    LocalDate birthDate;
    String password;
    String confirmPassword;
}
