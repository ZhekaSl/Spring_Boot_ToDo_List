package ua.zhenya.todo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.time.LocalDate;

@Value
public class RegistrationUserRequest {
    String email;
    String firstname;
    LocalDate birthDate;
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов!")
    String password;
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов!")
    String confirmPassword;
}
