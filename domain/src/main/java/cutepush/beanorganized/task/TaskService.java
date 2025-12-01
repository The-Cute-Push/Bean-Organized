package cutepush.beanorganized.task;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskEntity save(TaskEntity task) {
        return taskRepository.save(task);
    }

    public Iterable<TaskEntity> findAllByUserId(Long userId) {
        return taskRepository.findAllByUser_Id(userId);
    }

    public Optional<TaskEntity> findByIdAndUserId(Long id, Long userId) {
        return taskRepository.findByIdAndUser_Id(id, userId);
    }

    public void delete(TaskEntity entity) {
        taskRepository.delete(entity);
    }

    public Iterable<TaskEntity> findByUserAndFilters(Long userId, String title, Long categoryId, Instant from, Instant to) {
        // use Specification-based finder for unpaged result
        Specification<TaskEntity> spec = Specification.where(TaskSpecifications.byUserId(userId))
                .and(TaskSpecifications.titleLike(title))
                .and(TaskSpecifications.categoryId(categoryId))
                .and(TaskSpecifications.dueDateFrom(from))
                .and(TaskSpecifications.dueDateTo(to));
        return taskRepository.findAll(spec);
    }

    public Page<TaskEntity> findByUserAndFilters(Long userId, String title, Long categoryId, Instant from, Instant to, Pageable pageable) {
        Specification<TaskEntity> spec = Specification.where(TaskSpecifications.byUserId(userId))
                .and(TaskSpecifications.titleLike(title))
                .and(TaskSpecifications.categoryId(categoryId))
                .and(TaskSpecifications.dueDateFrom(from))
                .and(TaskSpecifications.dueDateTo(to));
        return taskRepository.findAll(spec, pageable);
    }

    public Page<TaskEntity> findAllByUserId(Long userId, Pageable pageable) {
        return taskRepository.findAllByUser_Id(userId, pageable);
    }
}
