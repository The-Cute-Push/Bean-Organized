package cutepush.beanorganized.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryEntity save(CategoryEntity category) {
        log.info("Saving category: {}", category);
        return categoryRepository.save(category);
    }

    public Iterable<CategoryEntity> findAllByUserId(Long userId) {
        return categoryRepository.findAllByUser_Id(userId);
    }

    public Optional<CategoryEntity> findByIdAndUserId(Long id, Long userId) {
        return categoryRepository.findByIdAndUser_Id(id, userId);
    }

    public void delete(CategoryEntity entity) {
        log.info("Deleting category: {}", entity);
        categoryRepository.delete(entity);
    }
}

