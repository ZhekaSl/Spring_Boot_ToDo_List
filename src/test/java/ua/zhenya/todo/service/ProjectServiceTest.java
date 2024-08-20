package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ua.zhenya.todo.dto.project.ProjectRequest;
import ua.zhenya.todo.intergration.IntegrationTestBase;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.motherObjects.TestEntityFactory;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.project.ProjectPermission;
import ua.zhenya.todo.project.UserProject;
import ua.zhenya.todo.project.UserProjectId;
import ua.zhenya.todo.repository.ProjectRepository;
import ua.zhenya.todo.repository.UserProjectRepository;
import ua.zhenya.todo.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest extends IntegrationTestBase {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProjectRepository userProjectRepository;

    private User defaultUser;
    private ProjectRequest defaultProjectRequest;

    @BeforeEach
    void setUp() {
        defaultUser = userRepository.save(TestEntityFactory.createDefaultUser());
        defaultProjectRequest = TestEntityFactory.createDefaultProjectRequest();
    }

    @Test
    void create_ShouldCreateProject() {
        Project createdProject = projectService.create(defaultUser.getId(), defaultProjectRequest);

        assertNotNull(createdProject);
        assertEquals(defaultProjectRequest.getName(), createdProject.getName());
        assertEquals(defaultUser, createdProject.getOwner());
    }

    @Test
    void update_ShouldUpdateProject() {
        Project project = projectService.create(defaultUser.getId(), defaultProjectRequest);
        ProjectRequest updateRequest = new ProjectRequest("Updated Project", "black");

        Project updatedProject = projectService.update(project.getId(), updateRequest);

        assertNotNull(updatedProject);
        assertEquals("Updated Project", updatedProject.getName());
        assertEquals("black", updatedProject.getColor());
    }

    @Test
    void addMember_ShouldAddMemberToProject() {
        Project project = projectRepository.save(TestEntityFactory.createDefaultProject(defaultUser));
        User newUser = TestEntityFactory.createDefaultUser();
        newUser.setEmail("top@gmail.com");
        newUser = userRepository.save(newUser);

        projectService.addMember(project.getId(), newUser.getId(), ProjectPermission.WRITE);

        Page<UserProject> members = userProjectRepository.findAllByProject(project, PageRequest.of(0, 10));
        assertEquals(1, members.getTotalElements());

        UserProject member = userProjectRepository.findByProjectAndUser(project, newUser).get();
        assertNotNull(member);
        assertEquals(newUser.getId(), member.getUser().getId());

        assertTrue(newUser.getUserProjects().contains(member));
        assertTrue(project.getUserProjects().contains(member));
    }

    @Test
    void removeMember_ShouldRemoveMemberFromProject() {
        Project project = projectRepository.save(TestEntityFactory.createDefaultProject(defaultUser));
        User newUser = userRepository.save(TestEntityFactory.createDefaultUser("user549@gmail.com"));

        UserProject userProject = new UserProject(new UserProjectId(newUser.getId(), project.getId()), newUser, project, ProjectPermission.WRITE);
        userProject.setProject(project);
        userProject.setUser(newUser);

        projectService.removeMember(project.getId(), newUser.getId());

        Page<UserProject> members = userProjectRepository.findAllByProject(project, PageRequest.of(0, 10));
        assertEquals(0, members.getTotalElements());

        assertFalse(userProjectRepository.findByProjectAndUser(project, newUser).isPresent());
    }

    @Test
    void removeMember_ShouldThrowExceptionWhenRemovingOwner() {
        Project project = projectRepository.save(TestEntityFactory.createDefaultProject(defaultUser));
        User owner = project.getOwner();

        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            projectService.removeMember(project.getId(), owner.getId());
        });

        String expectedMessage = "Владелец проекта не может удалить из проекта самого себя!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void removeMember_ShouldThrowExceptionWhenUserNotMemberOfProject() {
        Project project = projectRepository.save(TestEntityFactory.createDefaultProject(defaultUser));
        User newUser = userRepository.save(TestEntityFactory.createDefaultUser("user43@gmail.com "));

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            projectService.removeMember(project.getId(), newUser.getId());
        });

        String expectedMessage = "Пользователь не является участником проекта";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void findAll_ShouldReturnProjectsForUserAndSupportPagination() {
        Project project1 = projectRepository.save(TestEntityFactory.createDefaultProject(defaultUser));
        Project project2 = projectRepository.save(TestEntityFactory.createDefaultProject(defaultUser));
        Project project3 = projectRepository.save(TestEntityFactory.createDefaultProject(defaultUser));

        Page<Project> firstPage = projectService.findAll(defaultUser.getId(), PageRequest.of(0, 2));
        Page<Project> secondPage = projectService.findAll(defaultUser.getId(), PageRequest.of(1, 2));

        assertEquals(2, firstPage.getNumberOfElements());
        assertEquals(1, secondPage.getNumberOfElements());
        assertTrue(firstPage.getContent().contains(project1));
        assertTrue(firstPage.getContent().contains(project2));
        assertTrue(secondPage.getContent().contains(project3));
    }

    @Test
    void findAll_ShouldReturnEmptyPageIfNoProjectsForUser() {
        Page<Project> result = projectService.findAll(defaultUser.getId(), PageRequest.of(0, 2));

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllMembers_ShouldReturnMembersOfProjectAndSupportPagination() {
        Project project = projectRepository.save(TestEntityFactory.createDefaultProject(defaultUser));

        User user1 = userRepository.save(TestEntityFactory.createDefaultUser("user1@example.com"));
        User user2 = userRepository.save(TestEntityFactory.createDefaultUser("user2@example.com"));
        User user3 = userRepository.save(TestEntityFactory.createDefaultUser("user3@example.com"));

        projectService.addMember(project.getId(), user1.getId(), ProjectPermission.WRITE);
        projectService.addMember(project.getId(), user2.getId(), ProjectPermission.READ);
        projectService.addMember(project.getId(), user3.getId(), ProjectPermission.READ);

        Page<UserProject> firstPage = projectService.findAllMembers(project.getId(), PageRequest.of(0, 2));
        Page<UserProject> secondPage = projectService.findAllMembers(project.getId(), PageRequest.of(1, 2));

        assertEquals(2, firstPage.getNumberOfElements());
        assertEquals(1, secondPage.getNumberOfElements());
        assertTrue(firstPage.getContent().stream().anyMatch(up -> up.getUser().equals(user1)));
        assertTrue(firstPage.getContent().stream().anyMatch(up -> up.getUser().equals(user2)));
        assertTrue(secondPage.getContent().stream().anyMatch(up -> up.getUser().equals(user3)));
    }

    @Test
    void findAllMembers_ShouldReturnEmptyPageIfNoMembersInProject() {
        Project project = projectRepository.save(TestEntityFactory.createDefaultProject(defaultUser));

        Page<UserProject> result = projectService.findAllMembers(project.getId(), PageRequest.of(0, 2));

        assertTrue(result.isEmpty());
    }

    @Test
    void delete_ShouldRemoveProjectAndUpdateUser() {
        Project project = projectRepository.save(TestEntityFactory.createDefaultProject(defaultUser));
        projectService.delete(project.getId());

        Optional<Project> deletedProject = projectRepository.findById(project.getId());
        assertFalse(deletedProject.isPresent());

        User updatedOwner = userRepository.findById(defaultUser.getId()).get();
        assertFalse(updatedOwner.getProjects().contains(project));
    }

}