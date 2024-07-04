package ua.zhenya.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.zhenya.todo.dto.user.LoginUserRequest;
import ua.zhenya.todo.dto.user.RegistrationUserRequest;
import ua.zhenya.todo.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid RegistrationUserRequest registrationUserRequest) {
        return authService.createUser(registrationUserRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserRequest loginDTO) {
        return authService.createAuthToken(loginDTO);

    }
}

