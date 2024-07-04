package ua.zhenya.todo.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Value;

import java.time.LocalDate;

@Value
public class UserUpdateRequest {
    String username;
    String firstName;
    LocalDate birthDate;
    String oldPassword;
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов!")
    String newPassword;
}
