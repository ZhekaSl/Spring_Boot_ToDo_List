package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.user.RegistrationUserDTO;
import ua.zhenya.todo.dto.user.UserReadDTO;
import ua.zhenya.todo.dto.user.UserUpdateDTO;
import ua.zhenya.todo.mappers.user.UserCreateMapper;
import ua.zhenya.todo.mappers.user.UserReadMapper;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.service.UserService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserReadMapper userReadMapper;

    @GetMapping
    public PageResponse<UserReadDTO> findAll(Pageable pageable) {
        Page<UserReadDTO> page = userService.findAll(pageable)
                .map(userReadMapper::map);
        return PageResponse.of(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserReadDTO> findById(@PathVariable Integer id) {
        UserReadDTO userReadDTO = userReadMapper
                .map(userService.findById(id));
        return ResponseEntity.ok(userReadDTO);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserReadDTO> update(@PathVariable Integer id,
                                              @RequestBody UserUpdateDTO userDTO) {
        UserReadDTO user = userReadMapper
                .map(userService.update(id, userDTO));
        return ResponseEntity.ok(user);
    }

/*    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return userService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }*/
}
