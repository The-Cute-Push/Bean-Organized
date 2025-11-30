package cutepush.beanorganized.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long taskId;
    private Instant dateHour;
    private String type;
    private boolean sent;
}
