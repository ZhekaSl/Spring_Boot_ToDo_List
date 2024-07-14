package ua.zhenya.todo.mappers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.zhenya.todo.dto.checklist.ChecklistItemCreateRequest;
import ua.zhenya.todo.dto.checklist.ChecklistItemResponse;
import ua.zhenya.todo.model.ChecklistItem;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ChecklistItemMapperTest {
    @Autowired
    private ChecklistItemMapper checklistItemMapper;

    @Test
    void shouldProperlyMapItemRequestToItem() {
        ChecklistItemCreateRequest itemCreateRequest = new ChecklistItemCreateRequest(
                "Привет",
                LocalDate.of(2004, 5, 11),
                LocalTime.of(18, 42)
        );

        ChecklistItem item = checklistItemMapper.toEntity(itemCreateRequest);

        assertThat(item).isNotNull();
        assertEquals(itemCreateRequest.getTargetDate(), item.getTargetDate());
        assertEquals(itemCreateRequest.getTitle(), item.getTitle());
        assertEquals(itemCreateRequest.getTargetTime(), item.getTargetTime());
    }

    @Test
    void shouldProperlyMapItemToItemResponse() {
        ChecklistItem item = new ChecklistItem();
        item.setTitle("checklist1");
        item.setCompleted(true);
        item.setId(1);
        item.setTargetDate(LocalDate.now());

        ChecklistItemResponse checklistItemResponse = checklistItemMapper.toResponse(item);

        assertThat(checklistItemResponse).isNotNull();
        assertEquals(item.getTitle(), checklistItemResponse.getTitle());
        assertEquals(item.getId(), checklistItemResponse.getId());
        assertEquals(item.getTargetDate(), checklistItemResponse.getTargetDate());
        assertNull(item.getTargetTime());
        assertNull(checklistItemResponse.getTargetTime());
    }

}