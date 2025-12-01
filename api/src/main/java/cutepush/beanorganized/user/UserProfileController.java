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
    public UserProfileResponse create(@PathVariable Long userId, @Valid @RequestBody UserProfileRequest request) {
        var user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (userProfileService.findByUserId(userId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Profile already exists for this user");
        }

        UserProfileEntity profile = new UserProfileEntity();
        profile.setUser(user);
        applyRequest(profile, request);

        UserProfileEntity saved = userProfileService.save(profile);
        return toDto(saved);
    }

    @GetMapping
    public UserProfileResponse get(@PathVariable Long userId) {
        UserProfileEntity profile = userProfileService.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        return toDto(profile);
    }

    @PutMapping
    public UserProfileResponse update(@PathVariable Long userId, @Valid @RequestBody UserProfileRequest request) {
        userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserProfileEntity existing = userProfileService.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        applyRequest(existing, request);
        UserProfileEntity saved = userProfileService.save(existing);
        return toDto(saved);
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
        if (request.getPhone() != null) {
            target.setPhone(request.getPhone().replaceAll("\\D", ""));
        } else {
            target.setPhone(null);
        }
    }

    private static UserProfileResponse toDto(UserProfileEntity p) {
        if (p == null) return null;
        return new UserProfileResponse(
                p.getId(),
                p.getProfilePhoto(),
                p.getBiography(),
                p.getPhone()
        );
    }
}
