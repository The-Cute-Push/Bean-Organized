package cutepush.beanorganized.user;

import cutepush.beanorganized.kafka.user.EventType;
import cutepush.beanorganized.kafka.user.UserEvent;
import cutepush.beanorganized.kafka.user.UserProducer;
import cutepush.beanorganized.task.TaskEntity;
import cutepush.beanorganized.task.TaskRepository;
import cutepush.beanorganized.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
        if (userEntity.getPassword() != null && !isBCrypt(userEntity.getPassword())) {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        }
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
        return pwd != null && pwd.length() == 60 &&
                (pwd.startsWith("$2a$") || pwd.startsWith("$2b$") ||
                        pwd.startsWith("$2y$") || pwd.startsWith("$2x$"));
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

    // New: batch load users with profiles and tasks to avoid N+1 queries when listing
    @Transactional(readOnly = true)
    public List<UserWithRelations> findAllWithProfilesAndTasks() {
        Iterable<UserEntity> usersIt = userRepository.findAll();
        List<UserEntity> users = new ArrayList<>();
        usersIt.forEach(users::add);

        if (users.isEmpty()) return Collections.emptyList();

        List<Long> userIds = users.stream()
                .map(UserEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Batch load tasks and profiles
        Iterable<TaskEntity> tasksIt = taskRepository.findAllByUser_IdIn(userIds);
        Iterable<UserProfileEntity> profilesIt = userProfileRepository.findAllByUser_IdIn(userIds);

        // Group tasks by user id
        Map<Long, List<TaskEntity>> tasksByUser = new HashMap<>();
        if (tasksIt != null) {
            for (TaskEntity t : tasksIt) {
                if (t.getUser() == null || t.getUser().getId() == null) continue;
                tasksByUser.computeIfAbsent(t.getUser().getId(), k -> new ArrayList<>()).add(t);
            }
        }

        // Map profiles by user id (one profile per user)
        Map<Long, UserProfileEntity> profileByUser = new HashMap<>();
        if (profilesIt != null) {
            for (UserProfileEntity p : profilesIt) {
                if (p.getUser() == null || p.getUser().getId() == null) continue;
                profileByUser.put(p.getUser().getId(), p);
            }
        }

        // Build result list preserving user order
        List<UserWithRelations> result = new ArrayList<>();
        for (UserEntity u : users) {
            UserProfileEntity profile = profileByUser.get(u.getId());
            List<TaskEntity> userTasks = tasksByUser.getOrDefault(u.getId(), List.of());
            result.add(new UserWithRelations(u, profile, userTasks));
        }

        return result;
    }
}
