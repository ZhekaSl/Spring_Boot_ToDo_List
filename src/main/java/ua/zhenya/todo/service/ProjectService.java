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
import ua.zhenya.todo.project.Inbox;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.project.ProjectPermission;
import ua.zhenya.todo.project.UserProject;
import ua.zhenya.todo.repository.ProjectRepository;

import java.util.List;

/*@Service
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

    public Project findById(String username, Integer projectId) {
        User user = userService.findByEmail(username);
        Project project = findById(projectId);
        verifyProjectOwner(project, user);
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Проект не найден"));
    }

    @Transactional
    public void createInbox(String username) {
        User user = userService.findByEmail(username);

        Inbox inbox = new Inbox();
        inbox.setOwner(user);
        inbox.setColor("#000000");
        inbox.setName("Inbox");


    }

    @Transactional
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Transactional
    public Project create(String username, ProjectRequest projectRequest) {
        User user = userService.findByEmail(username);
        Project project = projectMapper.toEntity(projectRequest);
        project.setOwner(user);

        return save(project);
    }
    @Transactional
    public Project update(String username, Integer id, ProjectRequest projectRequest) {
        User user = userService.findByEmail(username);
        Project project = findById(id);
        verifyProjectOwner(project, user);
        projectMapper.update(projectRequest, project);

        return save(project);

    }

    @Transactional
    public void addMember(User user, Project project, ProjectPermission permission) {
        UserProject userProject = UserProject.builder()
                .user(user)
                .project(project)
                .permission(permission)
                .build();
        project.getUserProjects().add(userProject);
        save(project);
    }
    @Transactional

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
}*/
