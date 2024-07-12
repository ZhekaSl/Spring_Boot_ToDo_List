package ua.zhenya.todo.mappers.checklist;

import org.springframework.stereotype.Component;
import ua.zhenya.todo.dto.checklist.ChecklistItemCreateRequest;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.model.ChecklistItem;

@Component
public class ChecklistCreateMapper implements Mapper<ChecklistItemCreateRequest, ChecklistItem> {
    @Override
    public ChecklistItem map(ChecklistItemCreateRequest object) {
        ChecklistItem checklistItem = new ChecklistItem();
        copy(object, checklistItem);

        return checklistItem;
    }

    @Override
    public ChecklistItem map(ChecklistItemCreateRequest fromObject, ChecklistItem toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    private void copy(ChecklistItemCreateRequest checkListItemCreateRequest, ChecklistItem checklistItem) {
        checklistItem.setTitle(checkListItemCreateRequest.getTitle());
        checklistItem.setTargetDate(checkListItemCreateRequest.getTargetDate());
        checklistItem.setTargetTime(checkListItemCreateRequest.getTargetTime());
    }
}
