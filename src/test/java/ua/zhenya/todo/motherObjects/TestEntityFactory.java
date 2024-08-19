package ua.zhenya.todo.motherObjects;

import ua.zhenya.todo.dto.project.ProjectRequest;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.dto.task.TaskDueDetailsDTO;
import ua.zhenya.todo.model.*;
import ua.zhenya.todo.project.BaseProject;
import ua.zhenya.todo.project.Inbox;
import ua.zhenya.todo.project.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class TestEntityFactory {

    public static User createDefaultUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setFirstname("John");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        return user;
    }

    public static User createDefaultUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setFirstname("John");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        return user;
    }

    public static Project createDefaultProject(User owner) {
        Project project = new Project();
        project.setName("Default Project");
        project.setColor("blue");
        project.setOwner(owner);
        owner.addProject(project);
        return project;
    }

    public static Inbox createInbox(User owner) {
        Inbox inbox = new Inbox();
        inbox.setName("Default Project");
        inbox.setColor("blue");
        inbox.setOwner(owner);
        owner.setInbox(inbox);
        return inbox;
    }

    public static Task createDefaultTask(User user, BaseProject project) {
        Task task = new Task();
        task.setName("Default Task");
        task.setDescription("This is a default task description.");
        task.setPriority(Priority.MEDIUM);
        task.setUser(user);
        task.setProject(project);

        TaskDueInfo dueInfo = new TaskDueInfo();
        dueInfo.setDueDateTime(ZonedDateTime.now().plusDays(1));
        dueInfo.setTimeZone("Europe/Kiev");
        task.setTaskDueInfo(dueInfo);

        user.addTask(task);
        project.addTask(task);

        return task;
    }

    public static Task createCompletedTask(User user, BaseProject project) {
        Task task = createDefaultTask(user, project);
        task.setCompleted(true);
        task.setCompletedDateTime(ZonedDateTime.now());
        return task;
    }

    public static Task createTaskWithSubtasks(User user, BaseProject project) {
        Task parentTask = createDefaultTask(user, project);

        Task subtask1 = new Task();
        subtask1.setName("Subtask 1");
        subtask1.setParentTask(parentTask);
        user.addTask(subtask1);
        project.addTask(subtask1);

        Task subtask2 = new Task();
        subtask2.setName("Subtask 2");
        subtask2.setParentTask(parentTask);
        user.addTask(subtask2);
        project.addTask(subtask2);

        Task subtask3 = new Task();
        subtask2.setName("Subtask 3");
        subtask2.setParentTask(parentTask);
        user.addTask(subtask3);
        project.addTask(subtask2);

        parentTask.addSubtask(subtask1);
        parentTask.addSubtask(subtask2);
        parentTask.addSubtask(subtask3);

        return parentTask;
    }

    public static Task createTaskSubtasksAndChecklistItems(User user, BaseProject project) {
        Task parentTask = createTaskWithSubtasks(user, project);

        ChecklistItem checklistItem1 = new ChecklistItem();
        checklistItem1.setTitle("Checklistitem 1");
        parentTask.addChecklistItem(checklistItem1);


        ChecklistItem checklistItem2 = new ChecklistItem();
        checklistItem2.setTitle("Checklistitem 2");
        parentTask.addChecklistItem(checklistItem2);


        ChecklistItem checklistItem3 = new ChecklistItem();
        checklistItem2.setTitle("Checklistitem 3");
        parentTask.addChecklistItem(checklistItem3);

        return parentTask;
    }

    public static TaskCreateRequest createDefaultTaskCreateRequest(String projectId) {
        TaskDueDetailsDTO dueDetailsDTO = new TaskDueDetailsDTO(
                LocalDateTime.now().plusDays(1),
                "Europe/Kiev"
        );

        return new TaskCreateRequest(
                "Default Task",
                "Default Task Description",
                dueDetailsDTO,
                Priority.MEDIUM,
                projectId
        );
    }

    public static ProjectRequest createDefaultProjectRequest() {
        return new ProjectRequest("MyProject", "blue");
    }
}
