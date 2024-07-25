package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.project.ProjectRequest;
import ua.zhenya.todo.mappers.ProjectMapper;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.*;
import ua.zhenya.todo.repository.ProjectRepository;
import ua.zhenya.todo.repository.UserProjectRepository;
import ua.zhenya.todo.utils.TaskUtils;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {
    //    private final BaseProjectRepository<Project> baseProjectRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectMapper projectMapper;
    private final UserProjectRepository userProjectRepository;

    public Project findById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Проект с айди: " + projectId + " не найден!"));
    }

    @HasPermission(ProjectPermission.READ)
    public Project findById(String username, String projectId) {
        return findById(projectId);


    }


    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Transactional
    public Project create(String username, ProjectRequest request) {
        User user = userService.findByEmail(username);

        Project project = projectMapper.toEntity(request);
        project.setOwner(user);

        return save(project);
    }

    @Transactional
    @HasPermission(ProjectPermission.WRITE)
    public Project update(String username, String id, ProjectRequest projectRequest) {
        Project project = findById(id);
        projectMapper.update(projectRequest, project);

        return save(project);
    }

    @Transactional
    @OwnerAccess
    public void addMember(String username, String projectId, Integer id, ProjectPermission permission) {
        User user = userService.findById(id);
        Project project = findById(projectId);

        UserProjectId userProjectId = new UserProjectId(user.getId(), project.getId());
        UserProject userProject = UserProject.builder()
                .id(userProjectId)
                .user(user)
                .project(project)
                .permission(permission)
                .build();
        project.addUser(userProject);
        save(project);
    }


    @Transactional
    @OwnerAccess
    public void delete(String username, String projectId) {
        User user = userService.findByEmail(username);
        Project project = findById(projectId);
        TaskUtils.verifyProjectOwner(project, user);
        projectRepository.delete(project);
    }

    public Page<Project> findAll(String username, Pageable pageable) {
        User user = userService.findByEmail(username);
        return projectRepository.findAllByUserId(user.getId(), pageable);
    }

    @HasPermission(ProjectPermission.READ)
    public Page<UserProject> findAllMembers(String username, String projectId, Pageable pageable) {
        User user = userService.findByEmail(username);
        Project project = findById(projectId);
        TaskUtils.verifyProjectAccess(project, user);

        return userProjectRepository.findAllByProject(project, pageable);
    }

    @Transactional
    @OwnerAccess
    public void removeMember(String username, String projectId, Integer userId) {
        Project project = findById(projectId);
        User member = userService.findById(userId);

        if (Objects.equals(project.getOwner(), member)) {
            throw new UnsupportedOperationException("Владелец проекта не может удалить из проекта самого себя!");
        }

        UserProject userProject = userProjectRepository.findByProjectAndUser(project, member)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не является участником проекта"));

        project.removeUser(userProject);
        save(project);
    }
}
