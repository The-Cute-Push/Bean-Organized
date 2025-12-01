package cutepush.beanorganized.user;

import cutepush.beanorganized.kafka.user.EventType;
import cutepush.beanorganized.kafka.user.UserEvent;
import cutepush.beanorganized.kafka.user.UserProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProducer userProducer;
    private final PasswordEncoder passwordEncoder;

    public UserEntity findByName(String name) {
        return userRepository.findByName(name);
    }

    public UserEntity save(UserEntity userEntity) {
        boolean isNew = (userEntity.getId() == null);
        if (userEntity.getPassword() != null && !isBCrypt(userEntity.getPassword())) {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        }
        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User {}: {}", isNew ? "created" : "updated", savedUser);
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

    public void delete(Long id) {
        UserEntity userToDelete = findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        log.info("User deleted: {}", id);
        userProducer.send(UserEvent.create(userToDelete, EventType.DELETE));
        userRepository.delete(userToDelete);
    }
}
