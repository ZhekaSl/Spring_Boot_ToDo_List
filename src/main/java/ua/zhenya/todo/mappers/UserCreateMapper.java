package ua.zhenya.todo.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ua.zhenya.todo.dto.UserCreateDto;
import ua.zhenya.todo.model.User;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserCreateMapper implements Mapper<UserCreateDto, User> {
    private final PasswordEncoder passwordEncoder;


    @Override
    public User map(UserCreateDto object) {
        User user = new User();
        copy(object, user);

        return user;
    }

    private void copy(UserCreateDto dto, User user) {
        user.setUsername(dto.getUsername());
        user.setFirstname(dto.getFirstName());
        user.setBirthDate(dto.getBirthDate());

        Optional.ofNullable(dto.getPassword())
                .filter(StringUtils::hasText)
                .map(passwordEncoder::encode)
                .ifPresent(user::setPassword);
    }

    @Override
    public User map(UserCreateDto formObject, User toObject) {
        copy(formObject, toObject);
        return toObject;
    }



}
