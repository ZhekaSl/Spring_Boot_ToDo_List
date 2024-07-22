package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.project.ProjectRequest;
import ua.zhenya.todo.dto.project.ProjectResponse;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService2 {
    public final ProjectRepository projectRepository;
    private final UserService userService;

    @Transactional
    public Project create(String username, ProjectRequest request) {
        User user = userService.findByEmail(username);
        Project project1 = new Project();
        project1.setOwner(user);
        project1.setName(request.getName());
        project1.setColor(request.getColor());
        return projectRepository.save(project1);
    }

}
