package ua.zhenya.todo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.zhenya.todo.dto.checklist.ChecklistItemCreateRequest;
import ua.zhenya.todo.dto.checklist.ChecklistItemResponse;
import ua.zhenya.todo.model.ChecklistItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChecklistItemMapper {

    @Mapping(target = "task", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", ignore = true)
    ChecklistItem toEntity(ChecklistItemCreateRequest checklistItemCreateRequest);

    void toEntity(ChecklistItemCreateRequest request, @MappingTarget ChecklistItem checklistItem);

    ChecklistItemResponse toResponse(ChecklistItem checklistItem);

    List<ChecklistItemResponse> toResponseList(List<ChecklistItem> checklistItems);

}
