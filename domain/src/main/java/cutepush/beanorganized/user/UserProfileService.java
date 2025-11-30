package cutepush.beanorganized.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public Optional<UserProfileEntity> findByUserId(Long userId) {
        return userProfileRepository.findByUser_Id(userId);
    }

    public Optional<UserProfileEntity> findByUser(UserEntity user) {
        return userProfileRepository.findByUser(user);
    }

    public UserProfileEntity save(UserProfileEntity profile) {
        return userProfileRepository.save(profile);
    }

    public Optional<UserProfileEntity> findById(Long id) {
        return userProfileRepository.findById(id);
    }

    public Iterable<UserProfileEntity> findAll() {
        return userProfileRepository.findAll();
    }

    public void deleteById(Long id) {
        userProfileRepository.deleteById(id);
    }
}
