package cutepush.beanorganized.kafka.user;

public class UserEventProcessingException extends RuntimeException {
    public UserEventProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
