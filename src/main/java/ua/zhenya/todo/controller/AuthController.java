package ua.zhenya.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.zhenya.todo.dto.user.LoginRequest;
import ua.zhenya.todo.dto.user.RegistrationUserRequest;
import ua.zhenya.todo.mappers.UserMapper;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.security.JwtResponse;
import ua.zhenya.todo.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid RegistrationUserRequest registrationUserRequest) {
        User user = authService.register(registrationUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginDTO) {
        JwtResponse jwtResponse = authService.login(loginDTO);
        return ResponseEntity.ok(jwtResponse);

    }
}

