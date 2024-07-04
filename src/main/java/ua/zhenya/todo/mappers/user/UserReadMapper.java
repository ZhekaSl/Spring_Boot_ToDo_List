package ua.zhenya.todo.mappers.user;

import org.springframework.stereotype.Component;
import ua.zhenya.todo.dto.user.UserReadResponse;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.model.User;

@Component
public class UserReadMapper implements Mapper<User, UserReadResponse> {
    @Override
    public UserReadResponse map(User object) {
        return new UserReadResponse(
                object.getId(),
                object.getUsername(),
                object.getFirstname(),
                object.getBirthDate()
        );
    }
}
