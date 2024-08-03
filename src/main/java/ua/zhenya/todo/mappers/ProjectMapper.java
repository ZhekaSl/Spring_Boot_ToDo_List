package ua.zhenya.todo.mappers;

import jakarta.persistence.MappedSuperclass;
import org.mapstruct.*;
import ua.zhenya.todo.dto.project.ProjectRequest;
import ua.zhenya.todo.dto.project.ProjectResponse;
import ua.zhenya.todo.project.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(ProjectRequest projectRequest, @MappingTarget Project project);

    Project toEntity(ProjectRequest projectRequest);

    @Mapping(target = "ownerId", expression = "java(project.getOwner().getId())")
    ProjectResponse toResponse(Project project);

}
