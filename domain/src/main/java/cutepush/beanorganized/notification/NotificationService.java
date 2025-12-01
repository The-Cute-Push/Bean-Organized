package cutepush.beanorganized.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationEntity save(NotificationEntity entity) {
        log.info("Saving notification: {}", entity);
        return notificationRepository.save(entity);
    }

    public Iterable<NotificationEntity> findAllByTaskId(Long taskId) {
        return notificationRepository.findAllByTask_Id(taskId);
    }

    public Optional<NotificationEntity> findByIdAndTaskId(Long id, Long taskId) {
        return notificationRepository.findByIdAndTask_Id(id, taskId);
    }

    public void delete(NotificationEntity entity) {
        log.info("Deleting notification: {}", entity);
        notificationRepository.delete(entity);
    }
}
