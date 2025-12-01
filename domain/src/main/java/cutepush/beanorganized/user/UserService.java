package cutepush.beanorganized.user;

import cutepush.beanorganized.kafka.user.EventType;
import cutepush.beanorganized.kafka.user.UserEvent;
import cutepush.beanorganized.kafka.user.UserProducer;
import cutepush.beanorganized.task.TaskRepository;
import cutepush.beanorganized.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProducer userProducer;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public UserEntity findByName(String name) {
        return userRepository.findByName(name);
    }

    public UserEntity save(UserEntity userEntity) {
        // Encode password before saving so stored password is always encoded
        if (userEntity.getPassword() != null && !isBCrypt(userEntity.getPassword())) {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        }

        boolean isNew = (userEntity.getId() == null);
        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User {}: {}", isNew ? "created" : "updated", savedUser.getName());
        if (isNew) {
            userProducer.send(UserEvent.create(savedUser, EventType.CREATE));
        } else {
            userProducer.send(UserEvent.create(savedUser, EventType.UPDATE));
        }

        return savedUser;
    }

    private boolean isBCrypt(String pwd) {
        return pwd.startsWith("$2a$") || pwd.startsWith("$2b$") || pwd.startsWith("$2y$");
    }

    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    public Iterable<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        UserEntity userToDelete = findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        // Delete tasks owned by the user first; TaskEntity has cascade REMOVE for notifications and categoryTasks
        var tasks = taskRepository.findAllByUser_Id(id);
        if (tasks != null) {
            log.info("Deleting {} tasks for user {}", ((java.util.Collection<?>) tasks).size(), id);
            taskRepository.deleteAll(tasks);
        }

        // Delete categories owned by the user next; CategoryEntity has cascade REMOVE for categoryTasks
        var categories = categoryRepository.findAllByUser_Id(id);
        if (categories != null) {
            log.info("Deleting {} categories for user {}", ((java.util.Collection<?>) categories).size(), id);
            categoryRepository.deleteAll(categories);
        }

        // Remove any associated profile to avoid FK/constraint or transient reference issues
        userProfileRepository.findByUser_Id(id).ifPresent(profile -> {
            log.info("Deleting profile {} for user {}", profile.getId(), id);
            userProfileRepository.delete(profile);
            // Make sure the in-memory reference on the user doesn't point to a deleted/transient instance
            userToDelete.setProfile(null);
        });

        log.info("User deleted: {}", id);
        userProducer.send(UserEvent.create(userToDelete, EventType.DELETE));
        // Use deleteById to issue a direct delete without relying on the possibly-managed entity
        userRepository.deleteById(id);
    }
}
