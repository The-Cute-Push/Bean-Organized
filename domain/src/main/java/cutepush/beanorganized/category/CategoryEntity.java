package cutepush.beanorganized.category;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import cutepush.beanorganized.user.UserEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "category")
public class CategoryEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserEntity user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "color", length = 50)
    private String color;
}