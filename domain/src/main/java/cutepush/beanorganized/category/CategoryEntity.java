package cutepush.beanorganized.category;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import cutepush.beanorganized.user.UserEntity;
import cutepush.beanorganized.categorytasks.CategoryTaskEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "category")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "color", length = 50)
    private String color;

    // CategoryTask associations — remove them when the category is removed to avoid orphaned associations
    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private java.util.List<CategoryTaskEntity> categoryTasks = new java.util.ArrayList<>();

    // Backwards-compatible constructor used by tests and existing code that don't provide the categoryTasks list
    public CategoryEntity(Long id, UserEntity user, String name, String color) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.color = color;
        this.categoryTasks = new java.util.ArrayList<>();
    }
}