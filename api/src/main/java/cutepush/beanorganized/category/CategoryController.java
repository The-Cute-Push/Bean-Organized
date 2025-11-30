package cutepush.beanorganized.category;

import cutepush.beanorganized.user.UserEntity;
import cutepush.beanorganized.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users/{userId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@PathVariable Long userId, @Valid @RequestBody CategoryRequest request) {
        UserEntity user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CategoryEntity entity = new CategoryEntity();
        entity.setUser(user);
        applyRequest(entity, request);
        CategoryEntity saved = categoryService.save(entity);
        return toResponse(saved);
    }

    @GetMapping
    public Iterable<CategoryResponse> list(@PathVariable Long userId) {
        ensureUserExists(userId);
        var all = categoryService.findAllByUserId(userId);
        return java.util.stream.StreamSupport.stream(all.spliterator(), false)
                .map(CategoryController::toResponse)
                .toList();
    }

    @GetMapping("/{categoryId}")
    public CategoryResponse get(@PathVariable Long userId, @PathVariable Long categoryId) {
        ensureUserExists(userId);
        CategoryEntity entity = categoryService.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        return toResponse(entity);
    }

    @PutMapping("/{categoryId}")
    public CategoryResponse update(@PathVariable Long userId, @PathVariable Long categoryId, @Valid @RequestBody CategoryRequest request) {
        UserEntity user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CategoryEntity existing = categoryService.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        existing.setUser(user);
        applyRequest(existing, request);
        CategoryEntity saved = categoryService.save(existing);
        return toResponse(saved);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId, @PathVariable Long categoryId) {
        ensureUserExists(userId);
        CategoryEntity existing = categoryService.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        categoryService.delete(existing);
    }

    private void ensureUserExists(Long userId) {
        if (userService.findById(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    private static void applyRequest(CategoryEntity entity, CategoryRequest request) {
        entity.setName(request.getName());
        entity.setColor(request.getColor());
    }

    private static CategoryResponse toResponse(CategoryEntity entity) {
        Long uid = entity.getUser() != null ? entity.getUser().getId() : null;
        return new CategoryResponse(
                entity.getId(),
                uid,
                entity.getName(),
                entity.getColor()
        );
    }
}

