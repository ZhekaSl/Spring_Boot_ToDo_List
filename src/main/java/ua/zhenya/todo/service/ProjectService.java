package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.project.ProjectPermission;
import ua.zhenya.todo.repository.ProjectRepository;

import java.security.Principal;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;

    public Project findById(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Проект не найден"));
    }

    @Transactional
    public Project createInbox(String username) {
        User user = userService.findByEmail(username);

        Project project = Project.builder()
                .name("Inbox")
                .isInbox(true)
                .user(user)
                .build();

        return save(project);
    }

    @Transactional
    public Project save(Project project) {
        return projectRepository.save(project);
    }




}
