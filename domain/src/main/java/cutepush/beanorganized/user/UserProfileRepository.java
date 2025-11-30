package cutepush.beanorganized.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfileEntity, Long> {
    Optional<UserProfileEntity> findByUser_Id(Long userId);
    Optional<UserProfileEntity> findByUser(UserEntity user);
}
