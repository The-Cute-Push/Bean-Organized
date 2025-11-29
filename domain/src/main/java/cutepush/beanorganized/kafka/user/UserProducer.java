package cutepush.beanorganized.kafka.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static cutepush.beanorganized.kafka.KafkaTopics.USER_EVENTS_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(UserEvent event) {
        try {
            String userJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(USER_EVENTS_TOPIC , userJson);
            String msg = switch (event.eventType()) {
                case CREATE -> "User registration event sent to Kafka: {}";
                case UPDATE -> "User updated event sent to Kafka: {}";
                case DELETE -> "User deleted event sent to Kafka: {}";
            };
            log.info(msg, userJson);
        } catch (Exception e) {
            log.error("Error sending user registration event to Kafka: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send user registration event to Kafka", e);
        }
    }
}
