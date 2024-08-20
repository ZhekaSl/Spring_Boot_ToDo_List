package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.user.UserResponse;
import ua.zhenya.todo.dto.user.UserUpdateRequest;
import ua.zhenya.todo.mappers.UserMapper;
import ua.zhenya.todo.security.expression.CustomSecurityExpression;
import ua.zhenya.todo.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public PageResponse<UserResponse> findAll(Pageable pageable) {
        Page<UserResponse> page = userService.findAll(pageable)
                .map(userMapper::toResponse);
        return PageResponse.of(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Integer id) {
        UserResponse userResponse = userMapper.toResponse(userService.findById(id));
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> update(@PathVariable Integer id,
                                               @RequestBody UserUpdateRequest userDTO) {
        UserResponse user = userMapper.toResponse(userService.update(id, userDTO));
        return ResponseEntity.ok(user);
    }

/*    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return userService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }*/
}
