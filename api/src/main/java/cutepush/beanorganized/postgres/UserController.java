package cutepush.beanorganized.postgres;

import cutepush.beanorganized.postgres.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cutepush.beanorganized.postgres.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public Users add(@RequestBody Users users) {
        return service.save(users);
    }
}
