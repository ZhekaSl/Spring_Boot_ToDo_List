I am pleased to present to you the Todolist application for task management.

Technologies used: Java, Spring Framework (Spring Boot, Spring REST, Spring Security, Spring Data JPA), Hibernate, Maven, Liquibase, Postgres, MapStruct, FreeMarker, Swagger, JUnit, Mockito, Testcontainers, Docker.
Authentication: JWT.
The project is regularly updated.

Number of classes in the project: 110+.

The application's functionality includes:
1. Projects:
- Roles in the project: WRITE, READ. With WRITE, a user can perform any action on tasks. With READ, the user can only view them.
- Invite users via email with READ or WRITE roles.
- Remove a user.
- View a list of all participants.
- Change a participant's role.
2. Inbox:
- The Inbox is also a project, but it lacks the ability to invite users. It is created by default during user registration and cannot be deleted.
3. Invitations:
- The project owner can invite any user via email to join the project.
- The invited user receives an email and can either accept or decline the invitation.
- Users can view all invitations sent to them and the ones they have sent.
4. Tasks:
- When creating a task, you can specify a time zone, allowing each user to have their own deadline, ensuring that email notifications are sent at the correct time.
5. Subtasks, which are linked to a parent task.
6. Checklists, to break down a task into smaller parts. If all checklist items are completed, the task is automatically marked as complete.
7. Email notifications:
- Registration email.
- Email containing tasks whose deadline has passed.
- Project invitation email.
- Notification email informing the user that an invitation to the project has been accepted/declined.
