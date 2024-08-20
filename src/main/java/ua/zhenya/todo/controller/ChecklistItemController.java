package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.zhenya.todo.dto.PageResponse;
import ua.zhenya.todo.dto.checklist.ChecklistItemCreateRequest;
import ua.zhenya.todo.dto.checklist.ChecklistItemResponse;
import ua.zhenya.todo.mappers.ChecklistItemMapper;
import ua.zhenya.todo.model.ChecklistItem;
import ua.zhenya.todo.service.ChecklistItemService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks/{taskId}/checklists")
public class ChecklistItemController {
    private final ChecklistItemService checklistItemService;
    private final ChecklistItemMapper checklistItemMapper;

    @PreAuthorize("@customSecurityExpression.canModifyTask(#taskId)")
    @PostMapping
    public ResponseEntity<ChecklistItemResponse> create(@PathVariable Integer taskId, ChecklistItemCreateRequest checkListItemCreateRequest) {
        ChecklistItem checklistItem = checklistItemService.create(taskId, checkListItemCreateRequest);
        ChecklistItemResponse checklistItemResponse = checklistItemMapper.toResponse(checklistItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(checklistItemResponse);
    }

    @PreAuthorize("@customSecurityExpression.canModifyTask(#taskId)")
    @DeleteMapping("/{checklistItemId}")
    public ResponseEntity<?> delete(@PathVariable Integer taskId, @PathVariable Integer checklistItemId) {
        checklistItemService.delete(taskId, checklistItemId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@customSecurityExpression.canModifyTask(#taskId)")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ChecklistItemResponse> complete(@PathVariable Integer taskId, @PathVariable Integer id) {
        ChecklistItem checklistItem = checklistItemService.complete(taskId, id);
        ChecklistItemResponse checklistItemResponse = checklistItemMapper.toResponse(checklistItem);
        return ResponseEntity.ok(checklistItemResponse);

    }
}
