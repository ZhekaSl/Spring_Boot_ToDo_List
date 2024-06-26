package ua.zhenya.todo.mappers;

import ua.zhenya.todo.dto.UserReadDTO;
import ua.zhenya.todo.model.User;

public class UserReadMapper implements Mapper<User, UserReadDTO>{
    @Override
    public UserReadDTO map(User object) {
        return new UserReadDTO(
                object.getId(),
                object.getUsername(),
                object.getFirstname(),
                object.getBirthDate(),
                object.getRegisteredAt()

        );
    }
}
