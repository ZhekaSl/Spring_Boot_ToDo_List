package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.zhenya.todo.model.Role;
import ua.zhenya.todo.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER").get();
    }

    public Role getAdminRole() {
        return roleRepository.findByName("ROLE_ADMIN").get();
    }
}
