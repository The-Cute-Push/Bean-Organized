package cutepush.beanorganized.user;

import cutepush.beanorganized.kafka.user.EventType;
import cutepush.beanorganized.kafka.user.UserEvent;
import cutepush.beanorganized.kafka.user.UserProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProducer userProducer;

    public UserEntity findByName(String name) {
        return userRepository.findByName(name);
    }

    public UserEntity save(UserEntity userEntity) {
        try {
            boolean isNew = (userEntity.getId() == null);

            UserEntity savedUser = userRepository.save(userEntity);
            log.info("User {}: {}", isNew ? "created" : "updated", savedUser);

            if (isNew) {
                userProducer.send(UserEvent.create(savedUser, EventType.CREATE));
            } else {
                userProducer.send(UserEvent.create(savedUser, EventType.UPDATE));
            }
            return savedUser;
        } catch(Exception e) {
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
    }

    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    public Iterable<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public void deleteById(Long id) {

        userRepository.deleteById(id);
    }
}
