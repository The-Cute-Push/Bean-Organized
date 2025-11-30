package cutepush.beanorganized.task;

import cutepush.beanorganized.user.UserEntity;
import cutepush.beanorganized.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users/{userId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@PathVariable Long userId, @Valid @RequestBody TaskRequest request) {
        UserEntity user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        TaskEntity task = new TaskEntity();
        task.setUser(user);
        applyRequest(task, request);
        TaskEntity saved = taskService.save(task);
        return toResponse(saved);
    }

    @GetMapping
    public Iterable<TaskResponse> list(@PathVariable Long userId) {
        ensureUserExists(userId);
        var all = taskService.findAllByUserId(userId);
        return java.util.stream.StreamSupport.stream(all.spliterator(), false)
                .map(TaskController::toResponse)
                .toList();
    }

    @GetMapping("/{taskId}")
    public TaskResponse get(@PathVariable Long userId, @PathVariable Long taskId) {
        ensureUserExists(userId);
        TaskEntity entity = taskService.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return toResponse(entity);
    }

    @PutMapping("/{taskId}")
    public TaskResponse update(@PathVariable Long userId, @PathVariable Long taskId, @Valid @RequestBody TaskRequest request) {
        UserEntity user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        TaskEntity existing = taskService.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        existing.setUser(user);
        applyRequest(existing, request);
        TaskEntity saved = taskService.save(existing);
        return toResponse(saved);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId, @PathVariable Long taskId) {
        ensureUserExists(userId);
        TaskEntity existing = taskService.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        taskService.delete(existing);
    }

    private void ensureUserExists(Long userId) {
        if (userService.findById(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    private static void applyRequest(TaskEntity task, TaskRequest request) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setStatus(request.getStatus());
    }

    private static TaskResponse toResponse(TaskEntity entity) {
        Long uid = entity.getUser() != null ? entity.getUser().getId() : null;
        return new TaskResponse(
                entity.getId(),
                uid,
                entity.getTitle(),
                entity.getDescription(),
                entity.getDateCreation(),
                entity.getDueDate(),
                entity.getStatus()
        );
    }
}
