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
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.repository.ChecklistItemRepository;
import ua.zhenya.todo.utils.TaskUtils;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChecklistItemService {
    private final ChecklistItemRepository checklistItemRepository;
    private final TaskService taskService;
    private final UserService userService;
    private final ChecklistItemMapper checklistItemMapper;
    private final ApplicationEventPublisher eventPublisher;


    public ChecklistItem findById(Integer id) {
        return checklistItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Checklist с id: " + id + " не найден!"));
    }

    public Page<ChecklistItem> findAll(Principal principal, Integer taskId, Pageable pageable) {
        User user = userService.findByUsername(principal.getName());
        Task task = taskService.findById(taskId);
        TaskUtils.verifyTaskOwner(task, user);

        return checklistItemRepository.findAllByTaskId(taskId, pageable);
    }

    @Transactional
    public ChecklistItem create(Principal principal, Integer taskId, ChecklistItemCreateRequest checklistItemCreateRequest) {
        User user = userService.findByUsername(principal.getName());
        Task task = taskService.findById(taskId);
        TaskUtils.verifyTaskOwner(task, user);

        TaskUtils.checkDateIfTimeIsPresent(checklistItemCreateRequest.getTargetDate(), checklistItemCreateRequest.getTargetTime());

        ChecklistItem checklistItem = checklistItemMapper.toEntity(checklistItemCreateRequest);
        task.addChecklistItem(checklistItem);

        return checklistItemRepository.save(checklistItem);
    }

    @Transactional
    public ChecklistItem update(Principal principal, Integer taskId, Integer checklistItemId, ChecklistItemCreateRequest checklistItemCreateRequest) {
        User user = userService.findByUsername(principal.getName());
        Task task = taskService.findById(taskId);
        TaskUtils.verifyTaskOwner(task, user);
        ChecklistItem checklistItem = findById(checklistItemId);
        checkTaskContainsChecklistItem(task, checklistItem);

        checklistItemMapper.toEntity(checklistItemCreateRequest, checklistItem);


        return checklistItemRepository.save(checklistItem);
    }

    @Transactional
    public void delete(Principal principal, Integer taskId, Integer checklistItemId) {
        User user = userService.findByUsername(principal.getName());
        Task task = taskService.findById(taskId);
        TaskUtils.verifyTaskOwner(task, user);

        ChecklistItem checklistItem = findById(checklistItemId);
        checkTaskContainsChecklistItem(task, checklistItem);

        task.removeChecklistItem(checklistItem);
        checklistItemRepository.delete(checklistItem);
    }


    @Transactional
    public ChecklistItem complete(Principal principal, Integer taskId, Integer checklistItemId) {
        User user = userService.findByUsername(principal.getName());
        Task task = taskService.findById(taskId);
        TaskUtils.verifyTaskOwner(task, user);
        ChecklistItem checklistItem = findById(checklistItemId);
        checkTaskContainsChecklistItem(task, checklistItem);
        checklistItem.setCompleted(!checklistItem.isCompleted());

        ChecklistItem updatedChecklistItem = checklistItemRepository.save(checklistItem);

        eventPublisher.publishEvent(new ChecklistItemStatusUpdatedEvent(this, updatedChecklistItem));

        return updatedChecklistItem;
    }

    private static void checkTaskContainsChecklistItem(Task task, ChecklistItem checklistItem) {
        if (!task.getChecklistItems().contains(checklistItem)) {
            throw new EntityNotFoundException("Checklist item с id: " + checklistItem.getId() + " не найден!");
        }
    }
}
