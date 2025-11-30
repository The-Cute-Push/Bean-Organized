package cutepush.beanorganized.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private Instant dateCreation;
    private Instant dueDate;
    private String status;
}
