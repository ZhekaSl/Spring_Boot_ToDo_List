package ua.zhenya.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<PageResponse<ChecklistItemResponse>> findAll(Principal principal, @PathVariable Integer taskId, Pageable pageable) {
        Page<ChecklistItemResponse> page = checklistItemService.findAll(principal, taskId, pageable)
                .map(checklistItemMapper::toResponse);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @PostMapping
    public ResponseEntity<ChecklistItemResponse> create(Principal principal, @PathVariable Integer taskId, ChecklistItemCreateRequest checkListItemCreateRequest) {
        ChecklistItem checklistItem = checklistItemService.create(principal, taskId, checkListItemCreateRequest);
        ChecklistItemResponse checklistItemResponse = checklistItemMapper.toResponse(checklistItem);
        return ResponseEntity.ok(checklistItemResponse);
    }

    @DeleteMapping("/{checklistItemId}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable Integer taskId, @PathVariable Integer checklistItemId) {
        checklistItemService.delete(principal, taskId, checklistItemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ChecklistItemResponse> complete(Principal principal, @PathVariable Integer taskId, @PathVariable Integer id) {
        ChecklistItem checklistItem = checklistItemService.complete(principal, taskId, id);
        ChecklistItemResponse checklistItemResponse = checklistItemMapper.toResponse(checklistItem);
        return ResponseEntity.ok(checklistItemResponse);

    }
}
