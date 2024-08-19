package ua.zhenya.todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.intergration.IntegrationTestBase;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.motherObjects.TestEntityFactory;
import ua.zhenya.todo.project.BaseProject;
import ua.zhenya.todo.repository.ProjectRepository;
import ua.zhenya.todo.repository.TaskRepository;
import ua.zhenya.todo.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskServiceTest extends IntegrationTestBase {

    @Autowired
    private SubtaskService subtaskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private User defaultUser;
    private BaseProject defaultProject;
    private Task parentTask;

    @BeforeEach
    void setUp() {
        defaultUser = userRepository.save(TestEntityFactory.createDefaultUser());
        defaultProject = projectRepository.save(TestEntityFactory.createDefaultProject(defaultUser));
        parentTask = taskRepository.save(TestEntityFactory.createTaskWithSubtasks(defaultUser, defaultProject));
    }

    @Test
    void create_ShouldCreateSubtaskSuccessfully() {
        TaskCreateRequest createRequest = TestEntityFactory.createDefaultTaskCreateRequest(defaultProject.getId());

        Task subtask = subtaskService.create(parentTask.getId(), createRequest);

        assertNotNull(subtask);
        assertEquals(parentTask.getId(), subtask.getParentTask().getId());
        assertEquals(defaultProject.getId(), subtask.getProject().getId());
        assertEquals(defaultUser.getId(), subtask.getUser().getId());
        assertTrue(parentTask.getSubtasks().contains(subtask));
    }

    @Test
    void findAll_ShouldReturnAllSubtasksForParentTask() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Task> result = subtaskService.findAll(parentTask.getId(), pageable);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
    }
}