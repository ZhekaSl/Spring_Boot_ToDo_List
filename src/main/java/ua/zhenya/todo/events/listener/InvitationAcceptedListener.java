package ua.zhenya.todo.events.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.events.event.InvitationAcceptedEvent;
import ua.zhenya.todo.project.Invitation;
import ua.zhenya.todo.service.ProjectService;

@Component
@RequiredArgsConstructor
public class InvitationAcceptedListener {
    private final ProjectService projectService;

    @EventListener
    @Transactional
    public void handleInvitationAcceptedEvent(InvitationAcceptedEvent event) {
        Invitation invitation = event.getInvitation();
        projectService.addMember(invitation.getProject().getId(), invitation.getToUser().getId(), invitation.getPermission());
    }
}
