package ua.zhenya.todo.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ua.zhenya.todo.dto.project.ProjectRequest;
import ua.zhenya.todo.project.Project;

@Mapper
public interface ProjectMapper {

    Project toEntity(ProjectRequest projectRequest);

    ProjectRequest toResponse(Project project);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(ProjectRequest projectRequest, @MappingTarget Project project);
}
