package cutepush.beanorganized.categorytasks;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTaskRequest {
    @NotNull
    private Long categoryId;
}

