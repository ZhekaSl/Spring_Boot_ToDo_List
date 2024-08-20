package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.zhenya.todo.dto.invitation.InvitationCreateRequest;
import ua.zhenya.todo.intergration.IntegrationTestBase;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.motherObjects.TestEntityFactory;
import ua.zhenya.todo.project.*;
import ua.zhenya.todo.repository.InvitationRepository;
import ua.zhenya.todo.repository.ProjectRepository;
import ua.zhenya.todo.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class InvitationServiceTest extends IntegrationTestBase {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private User fromUser;
    private User toUser;
    private Project project;

    @BeforeEach
    void setUp() {
        fromUser = userRepository.save(TestEntityFactory.createDefaultUser("fromUser@example.com"));
        toUser = userRepository.save(TestEntityFactory.createDefaultUser("toUser@example.com"));
        project = projectRepository.save(TestEntityFactory.createDefaultProject(fromUser));
    }

    @Test
    void findById_ShouldReturnInvitationIfExists() {
        Invitation invitation = invitationRepository.save(TestEntityFactory.createDefaultInvitation(fromUser, toUser, project));

        Invitation foundInvitation = invitationService.findById(invitation.getId());

        assertNotNull(foundInvitation);
        assertEquals(invitation.getId(), foundInvitation.getId());
    }

    @Test
    void findById_ShouldThrowExceptionIfNotFound() {
        int nonExistingId = -1;

        assertThrows(EntityNotFoundException.class, () -> invitationService.findById(nonExistingId));
    }

    @Test
    void create_ShouldCreateNewInvitation() {
        InvitationCreateRequest createRequest = new InvitationCreateRequest(toUser.getEmail(), ProjectPermission.WRITE);

        Invitation createdInvitation = invitationService.create(fromUser.getId(), project.getId(), createRequest);

        assertNotNull(createdInvitation);
        assertEquals(fromUser, createdInvitation.getFromUser());
        assertEquals(toUser, createdInvitation.getToUser());
        assertEquals(project, createdInvitation.getProject());
        assertEquals(InvitationStatus.PENDING, createdInvitation.getStatus());
    }

    @Test
    void create_ShouldThrowExceptionIfPendingInvitationExists() {
        invitationRepository.save(TestEntityFactory.createDefaultInvitation(fromUser, toUser, project));
        InvitationCreateRequest createRequest = new InvitationCreateRequest(toUser.getEmail(), ProjectPermission.WRITE);

        assertThrows(IllegalArgumentException.class, () -> invitationService.create(fromUser.getId(), project.getId(), createRequest));
    }

    @Test
    void findAllByProject_ShouldReturnInvitationsForProject() {
        invitationRepository.save(TestEntityFactory.createDefaultInvitation(fromUser, toUser, project));
        Pageable pageable = PageRequest.of(0, 10);

        Page<Invitation> invitations = invitationService.findAllByProject(project.getId(), pageable);

        assertEquals(1, invitations.getTotalElements());
    }

    @Test
    void findAllByToUser_ShouldReturnInvitationsForUser() {
        invitationRepository.save(TestEntityFactory.createDefaultInvitation(fromUser, toUser, project));
        Pageable pageable = PageRequest.of(0, 10);

        Page<Invitation> invitations = invitationService.findAllByToUser(toUser.getId(), pageable);

        assertEquals(1, invitations.getTotalElements());
    }

    @Test
    void accept_ShouldApproveInvitation() {
        Invitation invitation = invitationRepository.save(TestEntityFactory.createDefaultInvitation(fromUser, toUser, project));

        invitationService.accept(invitation.getId());

        Invitation updatedInvitation = invitationRepository.findById(invitation.getId()).orElseThrow();
        assertEquals(InvitationStatus.APPROVED, updatedInvitation.getStatus());
    }

    @Test
    void reject_ShouldRejectInvitation() {
        Invitation invitation = invitationRepository.save(TestEntityFactory.createDefaultInvitation(fromUser, toUser, project));

        invitationService.reject(invitation.getId());

        Invitation updatedInvitation = invitationRepository.findById(invitation.getId()).orElseThrow();
        assertEquals(InvitationStatus.REJECTED, updatedInvitation.getStatus());
    }

    @Test
    void accept_ShouldThrowExceptionIfAlreadyProcessed() {
        Invitation invitation = invitationRepository.save(TestEntityFactory.createDefaultInvitation(fromUser, toUser, project));
        invitation.setStatus(InvitationStatus.APPROVED);
        invitationRepository.save(invitation);

        assertThrows(UnsupportedOperationException.class, () -> invitationService.accept(invitation.getId()));
    }

    @Test
    void reject_ShouldThrowExceptionIfAlreadyProcessed() {
        Invitation invitation = invitationRepository.save(TestEntityFactory.createDefaultInvitation(fromUser, toUser, project));
        invitation.setStatus(InvitationStatus.REJECTED);
        invitationRepository.save(invitation);

        assertThrows(UnsupportedOperationException.class, () -> invitationService.reject(invitation.getId()));
    }

}