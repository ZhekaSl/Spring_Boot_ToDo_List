package ua.zhenya.todo.mappers.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ua.zhenya.todo.dto.user.UserUpdateRequest;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.model.User;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserUpdateMapper implements Mapper<UserUpdateRequest, User> {
    private final PasswordEncoder passwordEncoder;

    @Override
    public User map(UserUpdateRequest object) {
        User user = new User();

        copy(object, user);
        return user;
    }

    private void copy(UserUpdateRequest dto, User user) {
        user.setUsername(dto.getUsername());
        user.setFirstname(dto.getFirstName());
        user.setBirthDate(dto.getBirthDate());

        Optional.ofNullable(dto.getNewPassword())
                .filter(StringUtils::hasText)
                .map(passwordEncoder::encode)
                .ifPresent(user::setPassword);
    }

    @Override
    public User map(UserUpdateRequest fromObject, User toObject) {
        copy(fromObject, toObject);
        return toObject;
    }
}
