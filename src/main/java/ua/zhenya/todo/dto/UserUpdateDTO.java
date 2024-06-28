package ua.zhenya.todo.dto;

import lombok.Value;

import java.time.LocalDate;

@Value
public class UserUpdateDTO {
    String username;
    String firstName;
    LocalDate birthDate;
    String oldPassword;
    String newPassword;
}
