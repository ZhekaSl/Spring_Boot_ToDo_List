package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.zhenya.todo.model.MailType;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.repository.TaskRepository;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class ReminderImpl implements Reminder {
    private final TaskService taskService;
    private final MailService mailService;
    private final Duration duration = Duration.ofHours(1);

/*    @Scheduled(cron = " 0 * * * * *")*/
    @Override
    public void remind() {
        List<Task> tasks = taskService.getAllSoonTasks(duration);
        tasks.forEach(task -> {
            User user = task.getUser();
            Properties properties = new Properties();
            properties.setProperty("task.title", task.getName());
            properties.setProperty("task.description", task.getDescription());
            mailService.sendMail(user, MailType.REMINDER, properties);
        });
    }
}
