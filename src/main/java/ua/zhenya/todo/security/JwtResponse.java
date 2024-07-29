package ua.zhenya.todo.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private Integer id;
    private String username;
    private String accessToken;
    private String refreshToken;

}
