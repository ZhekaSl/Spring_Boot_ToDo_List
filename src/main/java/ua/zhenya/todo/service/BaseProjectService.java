package ua.zhenya.todo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zhenya.todo.project.BaseProject;
import ua.zhenya.todo.repository.BaseProjectRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BaseProjectService {
    private final BaseProjectRepository<BaseProject> baseProjectRepository;

    public BaseProject findById(String id) {
        return baseProjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Проект с айди " + id + " не найден!"));
    }

}
