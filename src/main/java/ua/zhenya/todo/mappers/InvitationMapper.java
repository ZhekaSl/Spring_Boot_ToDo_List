package ua.zhenya.todo.mappers;

import org.mapstruct.*;
import ua.zhenya.todo.dto.invitation.InvitationCreateRequest;
import ua.zhenya.todo.dto.invitation.InvitationResponse;
import ua.zhenya.todo.project.Invitation;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InvitationMapper {
    @Mapping(target = "status", expression = "java(InvitationStatus.PENDING)")
    Invitation toEntity(InvitationCreateRequest invitationCreateRequest);

    @Mapping(target = "fromEmail", expression = "java(invitation.getFromUser().getEmail())")
    @Mapping(target = "toEmail", expression = "java(invitation.getToUser().getEmail())")
    InvitationResponse toResponse(Invitation invitation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(InvitationCreateRequest invitationCreateRequest, @MappingTarget Invitation invitation);
}