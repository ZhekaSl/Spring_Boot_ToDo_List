package ua.zhenya.todo.events.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ua.zhenya.todo.project.Invitation;

@Getter
public class InvitationRejectedEvent extends ApplicationEvent {
    private final Invitation invitation;

    public InvitationRejectedEvent(Object source, Invitation invitation) {
        super(source);
        this.invitation = invitation;
    }
}
