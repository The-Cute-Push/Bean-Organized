package cutepush.beanorganized.categorytasks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryTaskService {
    private final CategoryTaskRepository categoryTaskRepository;

    public CategoryTaskEntity save(CategoryTaskEntity entity) {
        return categoryTaskRepository.save(entity);
    }

    public Iterable<CategoryTaskEntity> findAllByCategoryId(Long categoryId) {
        return categoryTaskRepository.findAllByCategory_Id(categoryId);
    }

    public Iterable<CategoryTaskEntity> findAllByTaskId(Long taskId) {
        return categoryTaskRepository.findAllByTask_Id(taskId);
    }

    public Optional<CategoryTaskEntity> findByCategoryIdAndTaskId(Long categoryId, Long taskId) {
        return categoryTaskRepository.findByCategory_IdAndTask_Id(categoryId, taskId);
    }

    public Optional<CategoryTaskEntity> findByIdAndTaskId(Long id, Long taskId) {
        return categoryTaskRepository.findByIdAndTask_Id(id, taskId);
    }

    public void delete(CategoryTaskEntity entity) {
        categoryTaskRepository.delete(entity);
    }
}
