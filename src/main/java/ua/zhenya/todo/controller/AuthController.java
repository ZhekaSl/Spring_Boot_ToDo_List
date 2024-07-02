package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.zhenya.todo.dto.user.LoginUserDTO;
import ua.zhenya.todo.dto.user.RegistrationUserDTO;
import ua.zhenya.todo.service.AuthService;
import ua.zhenya.todo.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody RegistrationUserDTO registrationUserDTO) {
        return authService.createUser(registrationUserDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDTO loginDTO) {
        return authService.createAuthToken(loginDTO);

    }
}

