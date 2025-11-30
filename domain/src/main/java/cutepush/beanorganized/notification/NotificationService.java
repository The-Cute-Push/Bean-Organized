package cutepush.beanorganized.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationEntity save(NotificationEntity entity) {
        return notificationRepository.save(entity);
    }

    public Iterable<NotificationEntity> findAllByTaskId(Long taskId) {
        return notificationRepository.findAllByTask_Id(taskId);
    }

    public Optional<NotificationEntity> findByIdAndTaskId(Long id, Long taskId) {
        return notificationRepository.findByIdAndTask_Id(id, taskId);
    }

    public void delete(NotificationEntity entity) {
        notificationRepository.delete(entity);
    }
}
