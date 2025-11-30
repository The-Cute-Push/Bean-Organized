package cutepush.beanorganized.task;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends CrudRepository<TaskEntity, Long> {
    Iterable<TaskEntity> findAllByUser_Id(Long userId);
    Optional<TaskEntity> findByIdAndUser_Id(Long id, Long userId);
}
