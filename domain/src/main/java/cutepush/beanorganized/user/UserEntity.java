package cutepush.beanorganized.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private Instant dateCreation;

    private Timestamp lastLogin;

    // Expose the user's profile when serializing a User
    // Child side owns the relationship via @MapsId; this is the inverse side
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, optional = true)
    private UserProfileEntity profile;

    // Convenience constructor to preserve previous signatures used in tests
    public UserEntity(Long id, String name, String email, String password, Instant dateCreation, Timestamp lastLogin) {
        this(id, name, email, password, dateCreation, lastLogin, null);
    }
}
