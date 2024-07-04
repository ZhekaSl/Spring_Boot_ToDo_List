package ua.zhenya.todo.mappers.user;

import org.springframework.stereotype.Component;
import ua.zhenya.todo.dto.user.UserResponse;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.model.User;

@Component
public class UserReadMapper implements Mapper<User, UserResponse> {
    @Override
    public UserResponse map(User object) {
        return new UserResponse(
                object.getId(),
                object.getUsername(),
                object.getFirstname(),
                object.getBirthDate()
        );
    }
}
