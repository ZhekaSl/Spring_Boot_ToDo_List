package ua.zhenya.todo.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@AllArgsConstructor
public class JwtUserDetails implements UserDetails {
    private final Integer id;
    private final String name;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
}
