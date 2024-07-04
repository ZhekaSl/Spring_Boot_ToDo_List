package ua.zhenya.todo.mappers.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ua.zhenya.todo.dto.user.RegistrationUserRequest;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.model.User;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserCreateMapper implements Mapper<RegistrationUserRequest, User> {
    private final PasswordEncoder passwordEncoder;

    @Override
    public User map(RegistrationUserRequest object) {
        User user = new User();
        copy(object, user);

        return user;
    }

    private void copy(RegistrationUserRequest dto, User user) {
        user.setUsername(dto.getUsername());
        user.setFirstname(dto.getFirstName());
        user.setBirthDate(dto.getBirthDate());

        Optional.ofNullable(dto.getPassword())
                .filter(StringUtils::hasText)
                .map(passwordEncoder::encode)
                .ifPresent(user::setPassword);
    }

    @Override
    public User map(RegistrationUserRequest formObject, User toObject) {
        copy(formObject, toObject);
        return toObject;
    }



}
