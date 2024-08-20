package ua.zhenya.todo.mappers;

import org.mapstruct.*;
import ua.zhenya.todo.dto.invitation.InvitationCreateRequest;
import ua.zhenya.todo.dto.invitation.InvitationResponse;
import ua.zhenya.todo.project.Invitation;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InvitationMapper {
    @Mapping(target = "status", expression = "java(InvitationStatus.PENDING)")
    Invitation toEntity(InvitationCreateRequest invitationCreateRequest);

    @Mapping(target = "projectName", expression = "java(invitation.getProject().getName())")
    @Mapping(target = "projectId", expression = "java(invitation.getProject().getId())")
    @Mapping(target = "fromEmail", expression = "java(invitation.getFromUser().getEmail())")
    @Mapping(target = "toEmail", expression = "java(invitation.getToUser().getEmail())")
    InvitationResponse toResponse(Invitation invitation);
}