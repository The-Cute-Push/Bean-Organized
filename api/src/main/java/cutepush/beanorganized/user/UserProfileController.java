package cutepush.beanorganized.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users/{userId}/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final UserProfileService userProfileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfileEntity create(@PathVariable Long userId, @Valid @RequestBody UserProfileRequest request) {
        UserEntity user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));



        if (userProfileService.findByUserId(userId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Profile already exists for this user");
        }

        UserProfileEntity profile = new UserProfileEntity();
        request.setPhone(request.getPhone().replaceAll("\\D", ""));

        profile.setUser(user);
        // With @MapsId on the entity, the primary key will be derived from the associated user automatically
        applyRequest(profile, request);

        return userProfileService.save(profile);
    }

    @GetMapping
    public UserProfileEntity get(@PathVariable Long userId) {
        return userProfileService.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
    }

    @PutMapping
    public UserProfileEntity update(@PathVariable Long userId, @Valid @RequestBody UserProfileRequest request) {
        UserEntity user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserProfileEntity existing = userProfileService.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        existing.setUser(user); // ensure association
        applyRequest(existing, request);
        return userProfileService.save(existing);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        if (userProfileService.findByUserId(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }
        userProfileService.deleteById(userId);
    }

    private static void applyRequest(UserProfileEntity target, UserProfileRequest request) {
        target.setProfilePhoto(request.getProfilePhoto());
        target.setBiography(request.getBiography());
        target.setPhone(request.getPhone());
    }
}
