package ua.zhenya.todo.service.reminder;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.zhenya.todo.model.MailType;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.service.mail.MailService;
import ua.zhenya.todo.service.TaskService;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HourBeforeDueReminder implements Reminder {
    private final TaskService taskService;
    private final MailService mailService;
    private final Duration duration = Duration.ofHours(1);

    /*@Scheduled(cron = "0 * * * * *")*/
    @Override
    public void remind() {
        List<Task> tasks = taskService.findAllSoonTasks(duration);
        Map<User, List<Task>> tasksByUser = tasks.stream()
                .collect(Collectors.groupingBy(Task::getUser));

        tasksByUser.forEach((user, userTasks) -> {
            Properties properties = new Properties();

            properties.put("tasks", userTasks);
            properties.put("user", user);
            mailService.sendMail(user.getEmail(), MailType.HOUR_BEFORE_DUE_TASK_REMINDER, properties);
        });
    }
}
