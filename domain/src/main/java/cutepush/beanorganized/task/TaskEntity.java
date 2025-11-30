package cutepush.beanorganized.task;

import cutepush.beanorganized.user.UserEntity;
import cutepush.beanorganized.notification.NotificationEntity;
import cutepush.beanorganized.categorytasks.CategoryTaskEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "dateCreation", nullable = false, updatable = false)
    private Instant dateCreation;

    @Column(name = "dueDate", nullable = false)
    private Instant dueDate;

    @Column(name = "status", length = 50)
    private String status;

    // Notifications associated with this task — when a Task is removed, remove notifications as well.
    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<NotificationEntity> notifications = new ArrayList<>();

    // CategoryTask associations — remove them when the task is removed to avoid orphaned associations
    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CategoryTaskEntity> categoryTasks = new ArrayList<>();

    // Backwards-compatible constructor used by tests and existing code that don't provide the notifications list
    public TaskEntity(Long id, UserEntity user, String title, String description, Instant dateCreation, Instant dueDate, String status) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.description = description;
        this.dateCreation = dateCreation;
        this.dueDate = dueDate;
        this.status = status;
        this.notifications = new ArrayList<>();
        this.categoryTasks = new ArrayList<>();
    }
}
