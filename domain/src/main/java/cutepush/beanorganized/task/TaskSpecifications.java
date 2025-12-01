package cutepush.beanorganized.task;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import java.time.Instant;

public final class TaskSpecifications {

    private TaskSpecifications() {}

    public static Specification<TaskEntity> byUserId(Long userId) {
        return (root, query, cb) -> {
            // ensure distinct when joins are used
            if (query != null) query.distinct(true);
            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<TaskEntity> titleLike(String title) {
        if (title == null || title.isBlank()) return null;
        String pattern = "%" + title.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern);
    }

    public static Specification<TaskEntity> categoryId(Long categoryId) {
        if (categoryId == null) return null;
        return (root, query, cb) -> {
            // join to categoryTasks then to category
            if (query != null) query.distinct(true);
            return cb.equal(root.join("categoryTasks", JoinType.LEFT).join("category", JoinType.LEFT).get("id"), categoryId);
        };
    }

    public static Specification<TaskEntity> dueDateFrom(Instant from) {
        if (from == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dueDate"), from);
    }

    public static Specification<TaskEntity> dueDateTo(Instant to) {
        if (to == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("dueDate"), to);
    }
}
