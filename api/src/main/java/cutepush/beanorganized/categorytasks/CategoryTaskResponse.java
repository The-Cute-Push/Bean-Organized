package cutepush.beanorganized.categorytasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTaskResponse {
    private Long id;
    private Long categoryId;
    private Long taskId;
}

