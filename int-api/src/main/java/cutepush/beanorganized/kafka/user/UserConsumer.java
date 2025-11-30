package cutepush.beanorganized.kafka.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static cutepush.beanorganized.kafka.KafkaTopics.USER_EVENTS_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = USER_EVENTS_TOPIC, groupId = "bean-organized-group")
    public void consume(String message) {
        try {
            UserEvent event = objectMapper.readValue(message, UserEvent.class);
            log.info("User event received: {}", message);

            switch (event.eventType()) {
                case CREATE -> handleCreateEvent(event);
                case UPDATE -> handleUpdateEvent(event);
                case DELETE -> handleDeleteEvent(event);
            }
        } catch (Exception e) {
            log.error("Error processing user event from Kafka: {}", e.getMessage(), e);
            throw new UserEventProcessingException("Failed to process user event from Kafka", e);
        }
    }

    private void handleCreateEvent(UserEvent event) {
        log.info("Sending register email to user: {}", event.email());
        // TODO: Send email to new user
    }

    private void handleUpdateEvent(UserEvent event) {
        log.info("Sending update email to user: {}", event.email());
        // TODO: Send email to updated user
    }

    private void handleDeleteEvent(UserEvent event) {
        log.info("Sending deletion email to user: {}", event.email());
        // TODO: Send email to deleted user
    }
}
