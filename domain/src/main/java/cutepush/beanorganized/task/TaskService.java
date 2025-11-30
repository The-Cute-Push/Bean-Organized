package cutepush.beanorganized.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
