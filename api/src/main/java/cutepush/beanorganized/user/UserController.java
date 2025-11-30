package cutepush.beanorganized.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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
                .map(UserController::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        UserEntity user = userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(user);
    }

    private static UserResponse toResponse(UserEntity user) {
        UserProfileEntity profile = user.getProfile();
        UserResponse.Profile profileDto = null;
        if (profile != null) {
            profileDto = new UserResponse.Profile(
                    profile.getId(),
                    profile.getProfilePhoto(),
                    profile.getBiography(),
                    profile.getPhone()
            );
        }
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDateCreation(),
                user.getLastLogin(),
                profileDto
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