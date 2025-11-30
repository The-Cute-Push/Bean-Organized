package cutepush.beanorganized.category;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<CategoryEntity, Long> {
    Iterable<CategoryEntity> findAllByUser_Id(Long userId);
    Optional<CategoryEntity> findByIdAndUser_Id(Long id, Long userId);
}

