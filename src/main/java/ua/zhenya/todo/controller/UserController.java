package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.user.UserResponse;
import ua.zhenya.todo.dto.user.UserUpdateRequest;
import ua.zhenya.todo.mappers.user.UserReadMapper;
import ua.zhenya.todo.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserReadMapper userReadMapper;

    @GetMapping
    public PageResponse<UserResponse> findAll(Pageable pageable) {
        Page<UserResponse> page = userService.findAll(pageable)
                .map(userReadMapper::map);
        return PageResponse.of(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Integer id) {
        UserResponse userResponse = userReadMapper
                .map(userService.findById(id));
        return ResponseEntity.ok(userResponse);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> update(@PathVariable Integer id,
                                               @RequestBody UserUpdateRequest userDTO) {
        UserResponse user = userReadMapper
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
