package cutepush.beanorganized.notification;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends CrudRepository<NotificationEntity, Long> {
    Iterable<NotificationEntity> findAllByTask_Id(Long taskId);
    Optional<NotificationEntity> findByIdAndTask_Id(Long id, Long taskId);
}
