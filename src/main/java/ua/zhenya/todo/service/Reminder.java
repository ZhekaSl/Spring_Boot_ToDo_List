package ua.zhenya.todo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

public interface Reminder {
    void remind();
}
