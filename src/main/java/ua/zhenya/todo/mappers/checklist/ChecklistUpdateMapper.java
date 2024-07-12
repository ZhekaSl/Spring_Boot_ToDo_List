package ua.zhenya.todo.mappers.checklist;

import org.springframework.stereotype.Component;
import ua.zhenya.todo.dto.checklist.ChecklistItemCreateRequest;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.model.ChecklistItem;

@Component
public class ChecklistUpdateMapper implements Mapper<ChecklistItemCreateRequest, ChecklistItem> {
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
        if (checkListItemCreateRequest.getTitle() != null &&
            !checkListItemCreateRequest.getTitle().equals(checklistItem.getTitle())) {
            checklistItem.setTitle(checkListItemCreateRequest.getTitle());
        }

        handleTargetDateAndTime(checkListItemCreateRequest, checklistItem);
    }

    private void handleTargetDateAndTime(ChecklistItemCreateRequest request, ChecklistItem checklistItem) {
        if (request.getTargetDate() == null && request.getTargetTime() == null) {
            checklistItem.setTargetDate(null);
            checklistItem.setTargetTime(null);
        } else {
            if (request.getTargetDate() == null) {
                throw new IllegalArgumentException("Укажите сначала дату!");
            } else if (!request.getTargetDate().equals(checklistItem.getTargetDate())) {
                checklistItem.setTargetDate(request.getTargetDate());
            }
            if (request.getTargetTime() == null) {
                checklistItem.setTargetTime(null);
            } else if (!request.getTargetTime().equals(checklistItem.getTargetTime())) {
                checklistItem.setTargetTime(request.getTargetTime());
            }
        }
    }
}
