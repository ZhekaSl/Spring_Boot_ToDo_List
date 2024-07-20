package ua.zhenya.todo.mappers;

import org.mapstruct.*;
import ua.zhenya.todo.dto.project.ProjectRequest;
import ua.zhenya.todo.dto.project.ProjectResponse;
import ua.zhenya.todo.project.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toEntity(ProjectRequest projectRequest);

    @Mapping(target = "isInbox", source = "inbox")
    @Mapping(target = "ownerId", expression = "java(project.getOwner().getId())")
    ProjectResponse toResponse(Project project);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(ProjectRequest projectRequest, @MappingTarget Project project);

}
