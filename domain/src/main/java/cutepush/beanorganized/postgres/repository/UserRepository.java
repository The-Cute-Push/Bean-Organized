package cutepush.beanorganized.postgres.repository;

import cutepush.beanorganized.postgres.entity.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<Users, Long> {
    Users findByNome(String nome);
}
