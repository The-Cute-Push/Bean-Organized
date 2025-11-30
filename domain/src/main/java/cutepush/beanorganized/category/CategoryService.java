package cutepush.beanorganized.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryEntity save(CategoryEntity category) {
        return categoryRepository.save(category);
    }

    public Iterable<CategoryEntity> findAllByUserId(Long userId) {
        return categoryRepository.findAllByUser_Id(userId);
    }

    public Optional<CategoryEntity> findByIdAndUserId(Long id, Long userId) {
        return categoryRepository.findByIdAndUser_Id(id, userId);
    }

    public void delete(CategoryEntity entity) {
        categoryRepository.delete(entity);
    }
}

