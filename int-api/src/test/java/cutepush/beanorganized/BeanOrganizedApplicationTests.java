package cutepush.beanorganized;

import cutepush.beanorganized.category.CategoryRepository;
import cutepush.beanorganized.categorytasks.CategoryTaskRepository;
import cutepush.beanorganized.notification.NotificationRepository;
import cutepush.beanorganized.task.TaskRepository;
import cutepush.beanorganized.user.UserProfileRepository;
import cutepush.beanorganized.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
class BeanOrganizedApplicationTests {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserProfileRepository userProfileRepository;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private CategoryTaskRepository categoryTaskRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
    }

}
