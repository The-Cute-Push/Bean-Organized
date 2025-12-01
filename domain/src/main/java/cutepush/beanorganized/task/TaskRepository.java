package cutepush.beanorganized.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity> {
    Iterable<TaskEntity> findAllByUser_Id(Long userId);
    Optional<TaskEntity> findByIdAndUser_Id(Long id, Long userId);

    @Query("select distinct t from TaskEntity t inner join t.categoryTasks ct inner join ct.category c " +
            "where t.user.id = :userId " +
            "and (:title is null or lower(t.title) like lower(concat('%', :title, '%'))) " +
            "and (:categoryId is null or c.id = :categoryId) " +
            "and (:from is null or t.dueDate >= :from) " +
            "and (:to is null or t.dueDate <= :to)")
    Page<TaskEntity> findByUserAndFilters(@Param("userId") Long userId,
                                          @Param("title") String title,
                                          @Param("categoryId") Long categoryId,
                                          @Param("from") Instant from,
                                          @Param("to") Instant to,
                                          Pageable pageable);

    // paginated unfiltered finder
    Page<TaskEntity> findAllByUser_Id(Long userId, Pageable pageable);

    // Batch load tasks for multiple users to avoid N+1
    Iterable<TaskEntity> findAllByUser_IdIn(Collection<Long> userIds);
}
