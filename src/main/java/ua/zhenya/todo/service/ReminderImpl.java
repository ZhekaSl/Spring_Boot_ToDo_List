package ua.zhenya.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.zhenya.todo.model.MailType;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;

import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderImpl implements Reminder {
    private final TaskService taskService;
    private final MailService mailService;
    private final Duration duration = Duration.ofHours(1);

    @Scheduled(cron = "0 * * * * *")
    @Override
    public void remind() {
        List<Task> tasks = taskService.findAllSoonTasks(duration);
        Map<User, List<Task>> tasksByUser = tasks.stream()
                .collect(Collectors.groupingBy(Task::getUser));

        tasksByUser.forEach((user, userTasks) -> {
            Properties properties = new Properties();
            StringBuilder taskList = new StringBuilder();
            for (Task task : userTasks) {
                taskList.append("Задача: ").append(task.getName()).append("\n")
                        .append("Описание: ").append(task.getDescription()).append("\n")
                        .append("Дедлайн: ").append(task.getTaskDueInfo().getDueDateTime()).append("\n\n");
            }
            properties.setProperty("tasks", taskList.toString());

            mailService.sendMail(user, MailType.REMINDER, properties);
        });
    }
}
