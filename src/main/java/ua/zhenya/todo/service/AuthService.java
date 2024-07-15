package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.zhenya.todo.dto.user.JwtResponse;
import ua.zhenya.todo.dto.user.LoginUserRequest;
import ua.zhenya.todo.dto.user.RegistrationUserRequest;
import ua.zhenya.todo.exception.ErrorResponse;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> createAuthToken(LoginUserRequest loginUserRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserRequest.getEmail(), loginUserRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль!"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginUserRequest.getEmail());

        String token = jwtTokenService.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> createUser(RegistrationUserRequest registrationUserRequest) {
        try {
            userService.findByEmail(registrationUserRequest.getEmail());
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным email уже существует!"), HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.ok(userService.create(registrationUserRequest));
        }
    }
}