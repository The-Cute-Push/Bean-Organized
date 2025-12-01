package cutepush.beanorganized.task;

import cutepush.beanorganized.user.UserEntity;
import cutepush.beanorganized.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/users/{userId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Controller", description = "Controller for task CRUD")
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
    public Page<TaskResponse> list(@PathVariable Long userId,
                                   @RequestParam(required = false) String title,
                                   @RequestParam(required = false) Long categoryId,
                                   @RequestParam(required = false) String from,
                                   @RequestParam(required = false) String to,
                                   @PageableDefault(size = 20) Pageable pageable) {
        ensureUserExists(userId);

        Instant fromInstant = null;
        Instant toInstant = null;
        try {
            if (from != null && !from.isBlank()) {
                fromInstant = Instant.parse(from);
            }
            if (to != null && !to.isBlank()) {
                toInstant = Instant.parse(to);
            }
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format for 'from' or 'to'. Use ISO-8601 instant format, e.g. 2023-01-01T00:00:00Z");
        }

        boolean hasFilter = (title != null && !title.isBlank()) || categoryId != null || fromInstant != null || toInstant != null;
        Page<TaskEntity> page = hasFilter ? taskService.findByUserAndFilters(userId, title, categoryId, fromInstant, toInstant, pageable)
                : taskService.findAllByUserId(userId, pageable);

        return page.map(TaskController::toResponse);
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
