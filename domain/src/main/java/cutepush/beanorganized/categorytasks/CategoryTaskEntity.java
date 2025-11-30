package cutepush.beanorganized.categorytasks;

import cutepush.beanorganized.category.CategoryEntity;
import cutepush.beanorganized.task.TaskEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category_tasks")
public class CategoryTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "idCategory", nullable = false)
    private CategoryEntity category;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "idTasks", nullable = false)
    private TaskEntity task;
}

