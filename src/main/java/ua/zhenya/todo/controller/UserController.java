package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.zhenya.todo.dto.UserCreateDto;
import ua.zhenya.todo.dto.UserReadDTO;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserReadDTO> findAll() {
        return userService.findAll();

    }

    @GetMapping("/{id}")
    public UserReadDTO findById(@PathVariable Integer id) {
        return userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserReadDTO create(@RequestBody UserCreateDto user) {
        return userService.create(user);
    }

    @PutMapping("/{id}")
    public UserReadDTO update(@PathVariable Integer id, @RequestBody UserCreateDto user) {
        return null;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {

    }
}
