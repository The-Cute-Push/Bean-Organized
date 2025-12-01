package cutepush.beanorganized.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public Optional<UserProfileEntity> findByUserId(Long userId) {
        Optional<UserProfileEntity> opt = userProfileRepository.findByUser_Id(userId);
        // Force initialization of LOB fields while the persistence context is open to avoid LOB stream errors
        opt.ifPresent(p -> {
            if (p.getBiography() != null) {
                // Access the string to ensure Hibernate materializes the LOB
                p.getBiography();
            }
        });
        return opt;
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
