package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.zhenya.todo.dto.UserCreateDto;
import ua.zhenya.todo.model.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    @Transactional
    public User create(UserCreateDto user) {



    }

    public User findById(Integer id) {
    }

    public User update(Integer id, UserCreateDto user) {

    }
}
