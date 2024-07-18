package ua.zhenya.todo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.zhenya.todo.dto.user.RegistrationUserRequest;
import ua.zhenya.todo.dto.user.UserResponse;
import ua.zhenya.todo.dto.user.UserUpdateRequest;
import ua.zhenya.todo.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegistrationUserRequest registrationUserRequest);

    @Mapping(target = "password", source = "newPassword")
    void update(UserUpdateRequest userUpdateRequest, @MappingTarget User user);

    UserResponse toResponse(User user);
}
