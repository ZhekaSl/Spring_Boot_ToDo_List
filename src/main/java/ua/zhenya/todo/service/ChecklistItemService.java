package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.checklist.ChecklistItemCreateRequest;
import ua.zhenya.todo.events.event.ChecklistItemStatusUpdatedEvent;
import ua.zhenya.todo.mappers.ChecklistItemMapper;
import ua.zhenya.todo.model.ChecklistItem;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.repository.ChecklistItemRepository;
import ua.zhenya.todo.utils.TaskUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChecklistItemService {
    private final ChecklistItemRepository checklistItemRepository;
    private final TaskService taskService;
    private final ChecklistItemMapper checklistItemMapper;
    private final ApplicationEventPublisher eventPublisher;

    public ChecklistItem findById(Integer id) {
        return checklistItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Checklist с id: " + id + " не найден!"));
    }


    public Page<ChecklistItem> findAll(Integer taskId, Pageable pageable) {
        Task task = taskService.findById(taskId);

        return checklistItemRepository.findAllByTask(task, pageable);
    }

    @Transactional

    public ChecklistItem create(Integer taskId, ChecklistItemCreateRequest checklistItemCreateRequest) {
        Task task = taskService.findById(taskId);

        TaskUtils.checkDateIfTimeIsPresent(checklistItemCreateRequest.getTargetDate(), checklistItemCreateRequest.getTargetTime());

        ChecklistItem checklistItem = checklistItemMapper.toEntity(checklistItemCreateRequest);
        task.addChecklistItem(checklistItem);

        return checklistItemRepository.save(checklistItem);
    }

    @Transactional
    public ChecklistItem update(Integer taskId, Integer checklistItemId, ChecklistItemCreateRequest checklistItemCreateRequest) {
        Task task = taskService.findById(taskId);
        ChecklistItem checklistItem = findById(checklistItemId);
        checkTaskContainsChecklistItem(task, checklistItem);
        checklistItemMapper.toEntity(checklistItemCreateRequest, checklistItem);

        if (checklistItemCreateRequest.getTargetDate() == null && checklistItemCreateRequest.getTargetTime() == null) {
            task.setTargetDate(null);
            task.setTargetTime(null);
        } else {
            if (checklistItemCreateRequest.getTargetDate() == null) {
                throw new IllegalArgumentException("Укажите сначала дату!");
            }
            checklistItem.setTargetDate(checklistItemCreateRequest.getTargetDate());
            checklistItem.setTargetTime(checklistItemCreateRequest.getTargetTime());
        }


        return checklistItemRepository.save(checklistItem);
    }

    @Transactional

    public ChecklistItem complete(Integer taskId, Integer checklistItemId) {
        Task task = taskService.findById(taskId);
        ChecklistItem checklistItem = findById(checklistItemId);
        checkTaskContainsChecklistItem(task, checklistItem);
        checklistItem.setCompleted(!checklistItem.isCompleted());

        ChecklistItem updatedChecklistItem = checklistItemRepository.save(checklistItem);

        eventPublisher.publishEvent(new ChecklistItemStatusUpdatedEvent(this, updatedChecklistItem));

        return updatedChecklistItem;
    }

    @Transactional
    public void delete(Integer taskId, Integer checklistItemId) {
        Task task = taskService.findById(taskId);

        ChecklistItem checklistItem = findById(checklistItemId);
        checkTaskContainsChecklistItem(task, checklistItem);

        task.removeChecklistItem(checklistItem);
        checklistItemRepository.delete(checklistItem);
    }

    private static void checkTaskContainsChecklistItem(Task task, ChecklistItem checklistItem) {
        if (!task.getChecklistItems().contains(checklistItem)) {
            throw new EntityNotFoundException("Checklist item с id: " + checklistItem.getId() + " не найден!");
        }
    }
}
