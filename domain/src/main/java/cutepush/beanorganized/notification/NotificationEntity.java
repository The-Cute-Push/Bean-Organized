package cutepush.beanorganized.notification;

import cutepush.beanorganized.task.TaskEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tasksId", nullable = false)
    private TaskEntity task;

    @Column(name = "dateHour", nullable = false)
    private Instant dateHour;

    @Column(name = "type", length = 50, nullable = false)
    private String type; // ex: email, push, popup

    @Column(name = "sent", nullable = false)
    private boolean sent; // if it was sent or not
}
