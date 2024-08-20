package ua.zhenya.todo.motherObjects;

import ua.zhenya.todo.dto.checklist.ChecklistItemResponse;
import ua.zhenya.todo.dto.invitation.InvitationCreateRequest;
import ua.zhenya.todo.dto.project.ProjectRequest;
import ua.zhenya.todo.dto.task.SubtaskResponse;
import ua.zhenya.todo.dto.task.TaskCreateRequest;
import ua.zhenya.todo.dto.task.TaskDueDetailsDTO;
import ua.zhenya.todo.dto.task.TaskResponse;
import ua.zhenya.todo.model.*;
import ua.zhenya.todo.project.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public static TaskResponse createTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                createTaskDueDetailsDTO(task.getTaskDueInfo()),
                task.isCompleted(),
                task.getCompletedDateTime(),
                task.getPriority(),
                task.getParentTask() != null ? task.getParentTask().getId() : null,
                task.getProject() != null ? task.getProject().getId() : null,
                task.getUser() != null ? task.getUser().getId() : null,
                createChecklistItemResponses(task.getChecklistItems()),
                createSubtaskResponses(task.getSubtasks())
        );
    }

    private static TaskDueDetailsDTO createTaskDueDetailsDTO(TaskDueInfo taskDueInfo) {
        if (taskDueInfo == null) {
            return null;
        }
        return new TaskDueDetailsDTO(
                taskDueInfo.getDueDateTime().toLocalDateTime(),
                taskDueInfo.getTimeZone()
        );
    }

    private static List<ChecklistItemResponse> createChecklistItemResponses(List<ChecklistItem> checklistItems) {
        return checklistItems.stream()
                .map(item -> new ChecklistItemResponse(item.getId(), item.getTitle(), item.isCompleted()))
                .collect(Collectors.toList());
    }

    private static List<SubtaskResponse> createSubtaskResponses(List<Task> subtasks) {
        return subtasks.stream()
                .map(subtask -> new SubtaskResponse(
                        subtask.getId(),
                        subtask.getName(),
                        subtask.getPriority(),
                        createTaskDueDetailsDTO(subtask.getTaskDueInfo()),
                        subtask.isCompleted()
                ))
                .collect(Collectors.toList());
    }

    public static Invitation createDefaultInvitation(User fromUser, User toUser, Project project) {
        Invitation invitation = new Invitation();
        invitation.setFromUser(fromUser);
        invitation.setToUser(toUser);
        invitation.setProject(project);
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setPermission(ProjectPermission.READ);

        fromUser.addSentInvitation(invitation);
        toUser.addReceivedInvitation(invitation);
        project.addInvitation(invitation);
        return invitation;
    }

    public static InvitationCreateRequest createDefaultInvitationCreateRequest(User toUser, ProjectPermission permission) {
        return new InvitationCreateRequest(toUser.getEmail(), permission);
    }

    public static UserProject createDefaultUserProject(User user, Project project, ProjectPermission permission) {
        UserProjectId userProjectId = new UserProjectId(user.getId(), project.getId());

        UserProject userProject = new UserProject();
        userProject.setId(userProjectId);
        userProject.setUser(user);
        userProject.setProject(project);
        userProject.setPermission(permission);

        return userProject;
    }
}

