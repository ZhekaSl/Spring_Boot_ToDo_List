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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.zhenya.todo.dto.user.JwtResponse;
import ua.zhenya.todo.dto.user.LoginUserDTO;
import ua.zhenya.todo.dto.user.RegistrationUserDTO;
import ua.zhenya.todo.dto.user.UserReadDTO;
import ua.zhenya.todo.exception.AppError;
import ua.zhenya.todo.model.User;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> createAuthToken(LoginUserDTO loginUserDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль!"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginUserDTO.getUsername());

        String token = jwtTokenService.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> createUser(RegistrationUserDTO registrationUserDTO) {
        if (!registrationUserDTO.getPassword().equals(registrationUserDTO.getConfirmPassword())) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают!"), HttpStatus.BAD_REQUEST);
        }
        try {
            userService.findByUsername(registrationUserDTO.getUsername());
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует!"), HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.ok(userService.create(registrationUserDTO));
        }
    }
}