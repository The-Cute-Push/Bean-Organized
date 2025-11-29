package cutepush.beanorganized.kafka.user;

import cutepush.beanorganized.user.UserEntity;

import java.time.Instant;

public record UserEvent(Long id, String email, String name, Instant creationTime, EventType eventType) {

    public static UserEvent create(UserEntity userEntity, EventType eventType) {
        return new UserEvent(userEntity.getId(), userEntity.getEmail(), userEntity.getName(), userEntity.getDateCreation(), eventType);
    }
}
