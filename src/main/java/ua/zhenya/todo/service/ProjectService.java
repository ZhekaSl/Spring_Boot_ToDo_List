package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.project.ProjectRequest;
import ua.zhenya.todo.mappers.ProjectMapper;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.repository.ProjectRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectMapper projectMapper;

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
                .owner(user)
                .build();
        return save(project);
    }

    @Transactional
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public Project create(String username, ProjectRequest projectRequest) {
        User user = userService.findByEmail(username);
        Project project = projectMapper.toEntity(projectRequest);
        project.setOwner(user);

        return save(project);
    }

    public Project update(String username, Integer id, ProjectRequest projectRequest) {
        User user = userService.findByEmail(username);
        Project project = findById(id);
        verifyProjectOwner(project, user);
        projectMapper.update(projectRequest, project);

        return save(project);

    }

    public void delete(String username, Integer id) {
        User user = userService.findByEmail(username);
        Project project = findById(id);
        verifyProjectOwner(project, user);

        projectRepository.delete(project);
    }

    public Page<Project> findAll(String username, Pageable pageable) {
        User user = userService.findByEmail(username);
        return projectRepository.findByOwnerId(user.getId(), pageable);
    }

    private void verifyProjectOwner(Project project, User user) {
        if (!project.getOwner().equals(user)) {
            throw new AccessDeniedException("Вы не можете этого сделать!");
        }
    }


}
