package cutepush.beanorganized.postgres.service;

import cutepush.beanorganized.postgres.entity.Users;
import cutepush.beanorganized.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;

    public Users findByNome(String nome) {
        return repo.findByNome(nome);
    }

    public Users save(Users users) {
        return repo.save(users);
    }
}
