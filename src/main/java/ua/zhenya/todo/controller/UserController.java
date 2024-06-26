package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.UserCreateDto;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    @PostMapping
    public User create(@RequestBody UserCreateDto user) {
        return userService.create(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Integer id, @RequestBody UserCreateDto user) {
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {

    }
}
