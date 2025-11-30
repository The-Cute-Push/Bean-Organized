package cutepush.beanorganized.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.StreamSupport;
import java.util.stream.Collectors;

import cutepush.beanorganized.task.TaskEntity;
import cutepush.beanorganized.task.TaskService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserEntity add(@Valid @RequestBody UserRequest request) {
        UserEntity entity = new UserEntity();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPassword(request.getPassword());

        return userService.save(entity);
    }

    @GetMapping
    public Iterable<UserResponse> list() {
        Iterable<UserEntity> all = userService.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                // use a summary mapper that doesn't access user.getProfile() to avoid N+1 when profile is EAGER
                .map(UserController::toResponseSummary)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        UserEntity user = userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        // Load tasks for the given user and map to response DTOs
        Iterable<TaskEntity> all = taskService.findAllByUserId(id);
        var tasks = StreamSupport.stream(all.spliterator(), false)
                .map(t -> new UserResponse.Task(
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getDateCreation(),
                        t.getDueDate(),
                        t.getStatus()
                ))
                .collect(Collectors.toList());

        return toResponseWithTasks(user, tasks);
    }

    // Summary view used in list() to avoid loading relationships (prevents N+1)
    private static UserResponse toResponseSummary(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDateCreation(),
                user.getLastLogin(),
                null,
                null
        );
    }

    // Detailed response without tasks
    private static UserResponse toResponseWithoutTasks(UserEntity user) {
        UserResponse.Profile profileDto = toProfileDto(user.getProfile());
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDateCreation(),
                user.getLastLogin(),
                profileDto,
                null
        );
    }

    // Detailed response with tasks
    private static UserResponse toResponseWithTasks(UserEntity user, java.util.List<UserResponse.Task> tasks) {
        UserResponse.Profile profileDto = toProfileDto(user.getProfile());
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDateCreation(),
                user.getLastLogin(),
                profileDto,
                tasks
        );
    }

    // Helper to convert a UserProfileEntity to the DTO; returns null when profile is null
    private static UserResponse.Profile toProfileDto(UserProfileEntity profile) {
        if (profile == null) return null;
        return new UserResponse.Profile(
                profile.getId(),
                profile.getProfilePhoto(),
                profile.getBiography(),
                profile.getPhone()
        );
    }

    @PutMapping("/{id}")
    public UserEntity update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        Optional<UserEntity> optional = userService.findById(id);
        if (optional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        UserEntity existing = optional.get();
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPassword(request.getPassword());
        return userService.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}