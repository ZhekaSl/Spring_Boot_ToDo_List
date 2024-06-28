package ua.zhenya.todo.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class UserReadDTO {
    Integer id;
    String username;
    String firstname;
    LocalDate birthDate;
}
