package ua.zhenya.todo.service.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.zhenya.todo.model.MailType;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.service.mail.MailService;
import ua.zhenya.todo.service.TaskService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtDueTimeReminder implements Reminder{
    private final TaskService taskService;
    private final MailService mailService;

    @Scheduled(cron = "0 * * * * *")
    @Override
    public void remind() {
        log.info("Reminder job triggered at {}", LocalDateTime.now());
        List<Task> dueTasks = taskService.findAllSoonTasks(Duration.ZERO);

        Map<User, List<Task>> tasksByUser = dueTasks.stream()
                .collect(Collectors.groupingBy(Task::getUser));

        tasksByUser.forEach((user, userTasks) -> {
            Properties properties = new Properties();

            properties.put("tasks", userTasks);
            properties.put("user", user);
            mailService.sendMail(user.getEmail(), MailType.AT_DUE_TIME_TASK_REMINDER, properties);
        });
    }
}
