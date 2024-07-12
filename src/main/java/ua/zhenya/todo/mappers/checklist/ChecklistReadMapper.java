package ua.zhenya.todo.mappers.checklist;

import io.jsonwebtoken.impl.crypto.MacProvider;
import org.springframework.stereotype.Component;
import ua.zhenya.todo.dto.checklist.ChecklistItemResponse;
import ua.zhenya.todo.mappers.Mapper;
import ua.zhenya.todo.model.ChecklistItem;

@Component
public class ChecklistReadMapper implements Mapper<ChecklistItem, ChecklistItemResponse> {
    @Override
    public ChecklistItemResponse map(ChecklistItem object) {
        return new ChecklistItemResponse(
                object.getTitle(),
                object.isCompleted(),
                object.getTargetDate(),
                object.getTargetTime()
        );
    }
}
