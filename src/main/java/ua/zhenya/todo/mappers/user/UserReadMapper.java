package ua.zhenya.todo.mappers.user;

import org.springframework.stereotype.Component;
import ua.zhenya.todo.dto.user.UserReadDTO;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.model.User;

@Component
public class UserReadMapper implements Mapper<User, UserReadDTO> {
    @Override
    public UserReadDTO map(User object) {
        return new UserReadDTO(
                object.getId(),
                object.getUsername(),
                object.getFirstname(),
                object.getBirthDate()
        );
    }
}
