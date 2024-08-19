package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.dto.task.TaskDueDetailsDTO;
import ua.zhenya.todo.intergration.IntegrationTestBase;
import ua.zhenya.todo.model.Priority;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.motherObjects.TestEntityFactory;
import ua.zhenya.todo.project.BaseProject;
import ua.zhenya.todo.project.Project;
import ua.zhenya.todo.repository.*;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest extends IntegrationTestBase {
    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private InboxRepository inboxRepository;

    private User defaultUser;
    private Project defaultProject;
    private TaskCreateRequest defaultCreateRequest;
    private Task defaultTask;

    @BeforeEach
    void setUp() {
    }

    @Test
    void findById_ShouldReturnTaskIfExists() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        TaskCreateRequest createRequest = TestEntityFactory.createDefaultTaskCreateRequest(project.getId());

        Task createdTask = taskService.create(user.getId(), createRequest);

        Task foundTask = taskService.findById(createdTask.getId());
        assertNotNull(foundTask);
        assertEquals(createdTask.getId(), foundTask.getId());
    }

    @Test
    void findById_ShouldThrowExceptionIfNotExists() {
        int nonExistingTaskId = -1;
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.findById(nonExistingTaskId));
        assertEquals("Задача с id: " + nonExistingTaskId + " не найдена!", exception.getMessage());
    }

    @Test
    void create_ShouldCreateTaskAndAssociateWithUserAndProject() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));

        ZonedDateTime dueDateTime = ZonedDateTime.now().plusDays(1);
        TaskDueDetailsDTO dueDetailsDTO = new TaskDueDetailsDTO(dueDateTime.toLocalDateTime(), "Europe/Kiev");

        TaskCreateRequest createRequest = new TaskCreateRequest(
                "New Task",
                "Task Description",
                dueDetailsDTO,
                Priority.HIGH,
                project.getId()
        );

        Task createdTask = taskService.create(user.getId(), createRequest);

        assertNotNull(createdTask.getId());
        assertEquals(createRequest.getName(), createdTask.getName());

        assertEquals(project.getId(), createdTask.getProject().getId());
        assertEquals(user.getId(), createdTask.getUser().getId());


        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(updatedUser.getTasks().contains(createdTask));

        BaseProject updatedProject = projectRepository.findById(project.getId()).orElseThrow();
        assertTrue(updatedProject.getTasks().contains(createdTask));

        assertNotNull(createdTask.getTaskDueInfo());
        assertEquals(dueDateTime.toInstant(), createdTask.getTaskDueInfo().getDueDateTime().toInstant());
        assertEquals("Europe/Kiev", createdTask.getTaskDueInfo().getTimeZone());
    }

    @Test
    void create_ShouldThrowExceptionForInvalidUserOrProject() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        TaskCreateRequest createRequestWithInvalidProject = new TaskCreateRequest(
                "New Task",
                "Task Description",
                null,
                Priority.HIGH,
                "non-existing-project-id"
        );

        TaskCreateRequest createRequestWithValidProject = new TaskCreateRequest(
                "New Task",
                "Task Description",
                null,
                Priority.HIGH,
                project.getId()
        );

        assertThrows(EntityNotFoundException.class, () -> taskService.create(user.getId(), createRequestWithInvalidProject));
        assertThrows(EntityNotFoundException.class, () -> taskService.create(-1, createRequestWithValidProject));
    }

    @Test
    void findAll_ShouldReturnTasksForUserAndSupportPagination() {
        // Arrange
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));

        // Создаем 3 задачи
        Task task1 = taskRepository.save(TestEntityFactory.createDefaultTask(user, project));
        Task task2 = taskRepository.save(TestEntityFactory.createDefaultTask(user, project));
        Task task3 = taskRepository.save(TestEntityFactory.createDefaultTask(user, project));

        // Запрашиваем первую страницу с 2 задачами, сортируя по ID
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));

        // Act
        Page<Task> firstPage = taskService.findAll(user.getId(), pageable);

        // Запрашиваем вторую страницу с 1 задачей, сортируя по ID
        Pageable secondPageable = PageRequest.of(1, 2, Sort.by("id"));
        Page<Task> secondPage = taskService.findAll(user.getId(), secondPageable);

        // Assert
        assertNotNull(firstPage);
        assertEquals(2, firstPage.getNumberOfElements()); // На первой странице 2 задачи
        assertEquals(task1.getId(), firstPage.getContent().get(0).getId());
        assertEquals(task2.getId(), firstPage.getContent().get(1).getId());

        assertNotNull(secondPage);
        assertEquals(1, secondPage.getNumberOfElements()); // На второй странице 1 задача
        assertEquals(task3.getId(), secondPage.getContent().get(0).getId());
    }

    @Test
    void findAll_ShouldReturnEmptyPageIfNoTasks() {
        // Arrange
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> tasksPage = taskService.findAll(user.getId(), pageable);

        // Assert
        assertNotNull(tasksPage);
        assertTrue(tasksPage.isEmpty());
    }

    @Test
    void complete_ShouldToggleTaskCompletionAndSetCompletedDateTime() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        Task task = taskRepository.save(TestEntityFactory.createDefaultTask(user, project));

        Task completeTask = taskService.complete(task.getId());

        assertTrue(completeTask.isCompleted());
        assertNotNull(completeTask.getCompletedDateTime());

        Task reopenedTask = taskService.complete(task.getId());

        assertFalse(completeTask.isCompleted());
        assertNull(reopenedTask.getCompletedDateTime());

        Task foundTask = taskRepository.findById(task.getId()).orElseThrow();
        assertFalse(foundTask.isCompleted());
        assertNull(foundTask.getCompletedDateTime());
    }

    @Test
    void findAllSoonTasks_ShouldReturnTasksWithDeadlineInFuture() {
        // Arrange
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        Task task1 = TestEntityFactory.createDefaultTask(user, project);
        Task task2 = TestEntityFactory.createDefaultTask(user, project);
        task1.getTaskDueInfo().setDueDateTime(ZonedDateTime.now().plusDays(1));
        task2.getTaskDueInfo().setDueDateTime(ZonedDateTime.now().plusSeconds(1));
        taskRepository.save(task1);
        taskRepository.save(task2);

        // Act
        List<Task> result = taskService.findAllSoonTasks(Duration.ofDays(1));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findAllSoonTasks_ShouldReturnEmptyListIfNoTasksIfDeadLineInPast() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        Task task1 = TestEntityFactory.createDefaultTask(user, project);
        Task task2 = TestEntityFactory.createDefaultTask(user, project);
        task1.getTaskDueInfo().setDueDateTime(ZonedDateTime.now().plusHours(24).plusMinutes(1));
        task2.getTaskDueInfo().setDueDateTime(ZonedDateTime.now().plusHours(25));
        taskRepository.save(task1);
        taskRepository.save(task2);

        List<Task> result = taskService.findAllSoonTasks(Duration.ofDays(1));

        assertThat(result).isEmpty();
    }

    @Test
    void update_ShouldUpdateTaskFieldsCorrectly() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        Task task = taskRepository.save(TestEntityFactory.createDefaultTask(user, project));

        TaskCreateRequest updateRequest = new TaskCreateRequest(
                "Updated Task",
                "Updated Description",
                null,
                Priority.HIGH,
                project.getId()
        );

        Task updatedTask = taskService.update(task.getId(), updateRequest);

        assertNotNull(updatedTask);
        assertEquals(updateRequest.getName(), updatedTask.getName());
        assertEquals(updateRequest.getDescription(), updatedTask.getDescription());
        assertEquals(updateRequest.getPriority(), updatedTask.getPriority());
    }

    @Test
    void update_ShouldChangeProjectCorrectly() {
        // Arrange
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject oldProject = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        BaseProject newProject = inboxRepository.save(TestEntityFactory.createInbox(user));
        Task task = taskRepository.save(TestEntityFactory.createDefaultTask(user, oldProject));

        TaskCreateRequest updateRequest = new TaskCreateRequest(
                "Updated Task",
                "Updated Description",
                null,
                Priority.HIGH,
                newProject.getId()
        );

        // Act
        Task updatedTask = taskService.update(task.getId(), updateRequest);

        // Assert
        assertNotNull(updatedTask);
        assertEquals(newProject.getId(), updatedTask.getProject().getId());
        assertFalse(oldProject.getTasks().contains(updatedTask));
        assertTrue(newProject.getTasks().contains(updatedTask));
    }

    @Test
    void update_ShouldThrowExceptionWhenTaskNotFound() {
        TaskCreateRequest updateRequest = new TaskCreateRequest(
                "Updated Task",
                "Updated Description",
                null,
                Priority.HIGH,
                "project1"
        );
        assertThrows(EntityNotFoundException.class, () -> taskService.update(-1, updateRequest));
    }

    @Test
    void update_ShouldChangeSubtasksProjectCorrectlyIfParentTaskProjectIsChanged() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject oldProject = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        BaseProject newProject = inboxRepository.save(TestEntityFactory.createInbox(user));
        Task task = taskRepository.save(TestEntityFactory.createTaskWithSubtasks(user, oldProject));
        Task subtask = task.getSubtasks().get(0);
        subtask.addSubtask(TestEntityFactory.createDefaultTask(user, oldProject));
        taskRepository.save(subtask);
        Task subtaskOfSubtask = subtask.getSubtasks().get(0);

        TaskCreateRequest updateRequest = new TaskCreateRequest(
                "Updated Task",
                "Updated Description",
                null,
                Priority.HIGH,
                newProject.getId()
        );

        Task updatedTask = taskService.update(task.getId(), updateRequest);

        assertNotNull(updatedTask);
        for (Task t : task.getSubtasks()) {
            assertEquals(newProject.getId(), t.getProject().getId());
        }
        assertEquals(newProject.getId(), subtaskOfSubtask.getProject().getId());
    }
}


