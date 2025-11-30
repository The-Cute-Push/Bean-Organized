package cutepush.beanorganized.categorytasks;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryTaskRepository extends CrudRepository<CategoryTaskEntity, Long> {
    Iterable<CategoryTaskEntity> findAllByCategory_Id(Long categoryId);
    Iterable<CategoryTaskEntity> findAllByTask_Id(Long taskId);
    Optional<CategoryTaskEntity> findByCategory_IdAndTask_Id(Long categoryId, Long taskId);
    Optional<CategoryTaskEntity> findByIdAndTask_Id(Long id, Long taskId);
}
