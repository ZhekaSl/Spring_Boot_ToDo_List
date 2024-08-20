package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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

@ExtendWith(SpringExtension.class)
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
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));

        Task task1 = taskRepository.save(TestEntityFactory.createDefaultTask(user, project));
        Task task2 = taskRepository.save(TestEntityFactory.createDefaultTask(user, project));
        Task task3 = taskRepository.save(TestEntityFactory.createDefaultTask(user, project));

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));

        Page<Task> firstPage = taskService.findAll(user.getId(), pageable);

        Pageable secondPageable = PageRequest.of(1, 2, Sort.by("id"));
        Page<Task> secondPage = taskService.findAll(user.getId(), secondPageable);

        assertNotNull(firstPage);
        assertEquals(2, firstPage.getNumberOfElements());
        assertEquals(task1.getId(), firstPage.getContent().get(0).getId());
        assertEquals(task2.getId(), firstPage.getContent().get(1).getId());

        assertNotNull(secondPage);
        assertEquals(1, secondPage.getNumberOfElements());
        assertEquals(task3.getId(), secondPage.getContent().get(0).getId());
    }

    @Test
    void findAll_ShouldReturnEmptyPageIfNoTasks() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Task> tasksPage = taskService.findAll(user.getId(), pageable);

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
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        Task task1 = TestEntityFactory.createDefaultTask(user, project);
        Task task2 = TestEntityFactory.createDefaultTask(user, project);
        task1.getTaskDueInfo().setDueDateTime(ZonedDateTime.now().plusDays(1));
        task2.getTaskDueInfo().setDueDateTime(ZonedDateTime.now().plusSeconds(1));
        taskRepository.save(task1);
        taskRepository.save(task2);

        List<Task> result = taskService.findAllSoonTasks(Duration.ofDays(1));

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
    void update_ShouldUpdateProjectCorrectly() {
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

        Task updatedTask = taskService.update(task.getId(), updateRequest);

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
    @DisplayName("Should update subtask's project if parent task's project is updated")
    void update_ShouldUpdateSubtasksProjectCorrectlyIfParentTaskProjectIsUpdated() {
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

    @Test
    void update_ShouldNotUpdateProjectIfProjectIdIsNullOrTheSame() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        Task task = taskRepository.save(TestEntityFactory.createDefaultTask(user, project));

        TaskCreateRequest updateRequestWithNullProject = new TaskCreateRequest(
                task.getName(),
                task.getDescription(),
                null,
                task.getPriority(),
                null
        );

        Task updatedTask1 = taskService.update(task.getId(), updateRequestWithNullProject);

        assertEquals(project.getId(), updatedTask1.getProject().getId());
    }

    @Test
    @DisplayName("Should remove subtask from parent if subtask's project is changed")
    void update_ShouldRemoveTaskFromParentSubtasksWhenProjectChanges() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject oldProject = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        BaseProject newProject = projectRepository.save(TestEntityFactory.createDefaultProject(user));

        Task parentTask = taskRepository.save(TestEntityFactory.createTaskWithSubtasks(user, oldProject));

        Task subtask = parentTask.getSubtasks().get(0);

        TaskCreateRequest updateRequest = new TaskCreateRequest(
                subtask.getName(),
                subtask.getDescription(),
                null,
                subtask.getPriority(),
                newProject.getId()
        );
        Task updatedSubtask = taskService.update(subtask.getId(), updateRequest);

        assertNotNull(updatedSubtask);
        assertEquals(newProject.getId(), updatedSubtask.getProject().getId());
        assertFalse(parentTask.getSubtasks().contains(subtask));
        assertTrue(newProject.getTasks().contains(updatedSubtask));
        assertFalse(oldProject.getTasks().contains(updatedSubtask));
    }

    @Test
    void delete_ShouldRemoveTask() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        Task task = taskRepository.save(TestEntityFactory.createDefaultTask(user, project));

        taskService.delete(task.getId());

        assertFalse(taskRepository.findById(task.getId()).isPresent());
        assertFalse(user.getTasks().contains(task));
        assertFalse(project.getTasks().contains(task));
    }

    @Test
    void delete_ShouldRemoveTaskWithSubtasks() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        Task parentTask = taskRepository.save(TestEntityFactory.createTaskSubtasksAndChecklistItems(user, project));

        taskService.delete(parentTask.getId());

        assertFalse(taskRepository.findById(parentTask.getId()).isPresent());
        parentTask.getSubtasks().forEach(subtask ->
                assertFalse(taskRepository.findById(subtask.getId()).isPresent()));

        parentTask.getChecklistItems().forEach(checklistItem ->
                assertFalse(taskRepository.findById(checklistItem.getId()).isPresent()));
    }

    @Test
    void delete_ShouldDeleteSubtask() {
        User user = userRepository.save(TestEntityFactory.createDefaultUser());
        BaseProject project = projectRepository.save(TestEntityFactory.createDefaultProject(user));
        Task parentTask = taskRepository.save(TestEntityFactory.createTaskSubtasksAndChecklistItems(user, project));
        Task subtask = parentTask.getSubtasks().get(0);

        taskService.delete(subtask.getId());

        assertFalse(parentTask.getSubtasks().contains(subtask));
        assertFalse(taskRepository.findById(subtask.getId()).isPresent());
        assertTrue(taskRepository.findById(parentTask.getId()).isPresent());
    }

    @Test
    void delete_ShouldThrowExceptionWhenTaskNotFound() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.delete(-1));
        assertEquals("Задача с id: -1 не найдена!", exception.getMessage());
    }

}


