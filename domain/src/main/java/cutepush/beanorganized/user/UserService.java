package cutepush.beanorganized.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;

    public UserEntity findByName(String name) {
        return repo.findByName(name);
    }

    public UserEntity save(UserEntity userEntity) {
        return repo.save(userEntity);
    }
}
