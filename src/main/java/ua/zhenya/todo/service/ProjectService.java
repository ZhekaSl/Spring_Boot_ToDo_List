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



    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Transactional
    public Project create(Integer userId, ProjectRequest request) {
        User user = userService.findById(userId);

        Project project = projectMapper.toEntity(request);
        project.setOwner(user);

        return save(project);
    }

    @Transactional
    public Project update(String id, ProjectRequest projectRequest) {
        Project project = findById(id);
        projectMapper.update(projectRequest, project);

        return save(project);
    }

    @Transactional
    public void addMember(String projectId, Integer userId, ProjectPermission permission) {
        User user = userService.findById(userId);
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
    public void delete(String projectId) {
        Project project = findById(projectId);
        projectRepository.delete(project);
    }

    public Page<Project> findAll(Integer userId, Pageable pageable) {
        User user = userService.findById(userId);
        return projectRepository.findAllByUserId(user.getId(), pageable);
    }

    
    public Page<UserProject> findAllMembers(String projectId, Pageable pageable) {
        Project project = findById(projectId);

        return userProjectRepository.findAllByProject(project, pageable);
    }

    @Transactional
    public void removeMember(String projectId, Integer userId) {
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
