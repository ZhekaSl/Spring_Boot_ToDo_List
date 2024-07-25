package ua.zhenya.todo.events.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.events.event.ChecklistItemStatusUpdatedEvent;
import ua.zhenya.todo.model.ChecklistItem;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.repository.TaskRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ChecklistItemCompletionListener {
    private final TaskRepository taskRepository;

    @EventListener()
    @Transactional
    public void handleChecklistItemComplete(ChecklistItemStatusUpdatedEvent event) {
        ChecklistItem checklistItem = event.getChecklistItem();
        Task task = checklistItem.getTask();

        boolean allCompleted = task.getChecklistItems().stream().allMatch(ChecklistItem::isCompleted);
        if (allCompleted && !task.isCompleted()) {
            task.setCompleted(true);
            task.setCompletedDateTime(LocalDateTime.now());
            taskRepository.save(task);
        }
    }

    @EventListener
    @Transactional
    public void handleChecklistItemIncomplete(ChecklistItemStatusUpdatedEvent event) {
        ChecklistItem checklistItem = event.getChecklistItem();
        Task task = checklistItem.getTask();

        if (!task.getChecklistItems().stream().allMatch(ChecklistItem::isCompleted) && task.isCompleted()) {
            task.setCompleted(false);
            task.setCompletedDateTime(null);
            taskRepository.save(task);
        }
    }
}
