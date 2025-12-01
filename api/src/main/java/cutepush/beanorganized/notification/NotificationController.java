package cutepush.beanorganized.notification;

import cutepush.beanorganized.task.TaskEntity;
import cutepush.beanorganized.task.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users/{userId}/tasks/{taskId}/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "Controller for notification CRUD")
public class NotificationController {

    private final NotificationService notificationService;
    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse create(@PathVariable Long userId,
                                       @PathVariable Long taskId,
                                       @Valid @RequestBody NotificationRequest request) {
        TaskEntity task = taskService.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        NotificationEntity entity = new NotificationEntity();
        entity.setTask(task);
        applyRequest(entity, request);
        return toResponse(notificationService.save(entity));
    }

    @GetMapping
    public Iterable<NotificationResponse> list(@PathVariable Long userId,
                                               @PathVariable Long taskId) {
        ensureTaskBelongsToUser(taskId, userId);
        var all = notificationService.findAllByTaskId(taskId);
        return java.util.stream.StreamSupport.stream(all.spliterator(), false)
                .map(NotificationController::toResponse)
                .toList();
    }

    @GetMapping("/{notificationId}")
    public NotificationResponse get(@PathVariable Long userId,
                                    @PathVariable Long taskId,
                                    @PathVariable Long notificationId) {
        ensureTaskBelongsToUser(taskId, userId);
        NotificationEntity entity = notificationService.findByIdAndTaskId(notificationId, taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        return toResponse(entity);
    }

    @PutMapping("/{notificationId}")
    public NotificationResponse update(@PathVariable Long userId,
                                       @PathVariable Long taskId,
                                       @PathVariable Long notificationId,
                                       @Valid @RequestBody NotificationRequest request) {
        TaskEntity task = taskService.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        NotificationEntity existing = notificationService.findByIdAndTaskId(notificationId, taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        existing.setTask(task);
        applyRequest(existing, request);
        return toResponse(notificationService.save(existing));
    }

    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long taskId,
                       @PathVariable Long notificationId) {
        ensureTaskBelongsToUser(taskId, userId);
        NotificationEntity existing = notificationService.findByIdAndTaskId(notificationId, taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notificationService.delete(existing);
    }

    private void ensureTaskBelongsToUser(Long taskId, Long userId) {
        // also ensures user exists
        taskService.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    private static void applyRequest(NotificationEntity entity, NotificationRequest request) {
        entity.setDateHour(request.getDateHour());
        entity.setType(request.getType());
        entity.setSent(Boolean.TRUE.equals(request.getSent()));
    }

    private static NotificationResponse toResponse(NotificationEntity entity) {
        Long tid = entity.getTask() != null ? entity.getTask().getId() : null;
        return new NotificationResponse(
                entity.getId(),
                tid,
                entity.getDateHour(),
                entity.getType(),
                entity.isSent()
        );
    }
}
