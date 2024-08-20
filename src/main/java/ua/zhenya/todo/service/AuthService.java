package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.zhenya.todo.dto.user.LoginRequest;
import ua.zhenya.todo.dto.user.RegistrationUserRequest;
import ua.zhenya.todo.exception.ErrorResponse;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.security.JwtResponse;
import ua.zhenya.todo.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Неправильный логин или пароль!");
        }
        JwtResponse jwtResponse = new JwtResponse();
        User user = userService.findByEmail(loginRequest.getUsername());
        jwtResponse.setId(user.getId());
        jwtResponse.setUsername(user.getEmail());
        jwtResponse.setAccessToken(jwtTokenProvider.createAccessToken(
                user.getId(), user.getEmail(), user.getRoles()
        ));
        jwtResponse.setRefreshToken(jwtTokenProvider.createRefreshToken(
                user.getId(), user.getEmail()
        ));
        return jwtResponse;
    }

    public User register(RegistrationUserRequest registrationUserRequest) {
        return userService.create(registrationUserRequest);
    }

    public JwtResponse refresh(String refreshToken) {
        return jwtTokenProvider.refreshToken(refreshToken);
    }
}