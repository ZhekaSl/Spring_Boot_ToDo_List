package ua.zhenya.todo.events.event;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ua.zhenya.todo.model.ChecklistItem;

@Getter
public class ChecklistItemStatusUpdatedEvent extends ApplicationEvent {
    private final ChecklistItem checklistItem;

    public ChecklistItemStatusUpdatedEvent(Object source, ChecklistItem checklistItem) {
        super(source);
        this.checklistItem = checklistItem;
    }

}
