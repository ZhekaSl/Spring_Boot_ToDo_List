package ua.zhenya.todo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ua.zhenya.todo.dto.invitation.InvitationCreateRequest;
import ua.zhenya.todo.dto.invitation.InvitationResponse;
import ua.zhenya.todo.dto.user.UserProjectResponse;
import ua.zhenya.todo.project.Invitation;
import ua.zhenya.todo.project.UserProject;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProjectMapper {

    @Mapping(target = "firstname", expression = "java(userProject.getUser().getFirstname())")
    @Mapping(target = "email", expression = "java(userProject.getUser().getEmail())")
    @Mapping(target = "id", expression = "java(userProject.getUser().getId())")
    UserProjectResponse toResponse(UserProject userProject);

}