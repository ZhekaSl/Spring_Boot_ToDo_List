package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.dto.invitation.InvitationCreateRequest;
import ua.zhenya.todo.events.event.InvitationAcceptedEvent;
import ua.zhenya.todo.events.event.InvitationRejectedEvent;
import ua.zhenya.todo.mappers.InvitationMapper;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.project.*;
import ua.zhenya.todo.repository.InvitationRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final InvitationMapper invitationMapper;
    private final ApplicationEventPublisher eventPublisher;

    public Invitation findById(Integer id) {
        return invitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Приглашение с id: " + id + " не было найдено!"));
    }

    @Transactional
    public Invitation create(Integer fromUserId, String projectId, InvitationCreateRequest invitationCreateRequest) {
        User fromUser = userService.findById(fromUserId);
        User toUser = userService.findByEmail(invitationCreateRequest.getToEmail());
        Project project = projectService.findById(projectId);

        if (project.getOwner().equals(toUser)) {
            throw new IllegalArgumentException("Нельзя пригласить владельца проекта.");
        }

        boolean isUserMember = project.getUserProjects().stream()
                .anyMatch(up -> up.getUser().equals(toUser));
        if (isUserMember) {
            throw new IllegalArgumentException("Пользователь уже является участником проекта.");
        }

        boolean pendingInvitationExists = invitationRepository.existsPendingInvitation(project, toUser);
        if (pendingInvitationExists) {
            throw new IllegalArgumentException("Пользователь уже приглашен в проект.");
        }

        Invitation invitation = invitationMapper.toEntity(invitationCreateRequest);
        invitation.setFromUser(fromUser);
        invitation.setToUser(toUser);
        invitation.setProject(project);

        return invitationRepository.save(invitation);
    }

    public Page<Invitation> findAllByProject(String projectId, Pageable pageable) {
        Project project = projectService.findById(projectId);
        return invitationRepository.findAllByProject(project, pageable);
    }

    public Page<Invitation> findAllByToUser(Integer userId, Pageable pageable) {
        User user = userService.findById(userId);
        return invitationRepository.findAllByToUser(user, pageable);
    }

    @Transactional
    public void accept(Integer userId, Integer invitationId) {
        processResponse(userId, invitationId, InvitationStatus.APPROVED);

    }

    @Transactional
    public void reject(Integer userId, Integer invitationId) {
        processResponse(userId, invitationId, InvitationStatus.REJECTED);
    }

    private void processResponse(Integer userId, Integer invitationId, InvitationStatus status) {
        User user = userService.findById(userId);
        Invitation invitation = findById(invitationId);

        if (!Objects.equals(invitation.getToUser().getId(), user.getId())) {
            throw new AccessDeniedException("Это приглашение послано не Вам!");
        }

        if (invitation.getStatus() == InvitationStatus.APPROVED || invitation.getStatus() == InvitationStatus.REJECTED) {
            throw new UnsupportedOperationException("Вы уже обработали это приглашение ранее!");
        }

        invitation.setStatus(status);
        invitationRepository.save(invitation);

        if (status == InvitationStatus.APPROVED) {
            eventPublisher.publishEvent(new InvitationAcceptedEvent(this, invitation));
        } else if (status == InvitationStatus.REJECTED) {
            eventPublisher.publishEvent(new InvitationRejectedEvent(this, invitation));
        }
    }

}
