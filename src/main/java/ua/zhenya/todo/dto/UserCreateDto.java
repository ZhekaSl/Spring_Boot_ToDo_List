package ua.zhenya.todo.dto;

import lombok.Value;

import java.time.LocalDate;

@Value
public class UserCreateDto {
    String username;
    String firstName;
    LocalDate birthDate;
    String password;
    String confirmPassword;
}
