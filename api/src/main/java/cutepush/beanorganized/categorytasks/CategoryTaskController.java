package cutepush.beanorganized.categorytasks;

import cutepush.beanorganized.category.CategoryEntity;
import cutepush.beanorganized.category.CategoryService;
import cutepush.beanorganized.task.TaskEntity;
import cutepush.beanorganized.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users/{userId}/tasks/{taskId}/categories")
@RequiredArgsConstructor
public class CategoryTaskController {

    private final CategoryTaskService categoryTaskService;
    private final TaskService taskService;
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryTaskResponse> create(@PathVariable Long userId,
                                      @PathVariable Long taskId,
                                      @Valid @RequestBody CategoryTaskRequest request) {
        TaskEntity task = taskService.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        CategoryEntity category = categoryService.findByIdAndUserId(request.getCategoryId(), userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        // prevent duplicates
        var existing = categoryTaskService.findByCategoryIdAndTaskId(category.getId(), task.getId());
        if (existing.isPresent()) {
            // nothing was created — return 200 OK with the existing resource
            return ResponseEntity.ok(toResponse(existing.get()));
        }

        CategoryTaskEntity entity = new CategoryTaskEntity();
        entity.setCategory(category);
        entity.setTask(task);
        CategoryTaskEntity saved = categoryTaskService.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping
    public Iterable<CategoryTaskResponse> list(@PathVariable Long userId, @PathVariable Long taskId) {
        ensureTaskBelongsToUser(taskId, userId);
        var all = categoryTaskService.findAllByTaskId(taskId);
        return java.util.stream.StreamSupport.stream(all.spliterator(), false)
                .map(CategoryTaskController::toResponse)
                .toList();
    }

    @GetMapping("/{categoryTaskId}")
    public CategoryTaskResponse get(@PathVariable Long userId, @PathVariable Long taskId, @PathVariable Long categoryTaskId) {
        ensureTaskBelongsToUser(taskId, userId);
        CategoryTaskEntity entity = categoryTaskService.findByIdAndTaskId(categoryTaskId, taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category-Task association not found"));
        return toResponse(entity);
    }

    @DeleteMapping("/{categoryTaskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId, @PathVariable Long taskId, @PathVariable Long categoryTaskId) {
        ensureTaskBelongsToUser(taskId, userId);
        CategoryTaskEntity existing = categoryTaskService.findByIdAndTaskId(categoryTaskId, taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category-Task association not found"));
        categoryTaskService.delete(existing);
    }

    private void ensureTaskBelongsToUser(Long taskId, Long userId) {
        taskService.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    private static CategoryTaskResponse toResponse(CategoryTaskEntity entity) {
        Long cid = entity.getCategory() != null ? entity.getCategory().getId() : null;
        Long tid = entity.getTask() != null ? entity.getTask().getId() : null;
        return new CategoryTaskResponse(entity.getId(), cid, tid);
    }
}
