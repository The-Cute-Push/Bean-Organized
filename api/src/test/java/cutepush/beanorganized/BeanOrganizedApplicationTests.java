package cutepush.beanorganized;

import cutepush.beanorganized.category.CategoryService;
import cutepush.beanorganized.categorytasks.CategoryTaskService;
import cutepush.beanorganized.task.TaskService;
import cutepush.beanorganized.notification.NotificationService;
import cutepush.beanorganized.user.UserProfileRepository;
import cutepush.beanorganized.user.UserProfileService;
import cutepush.beanorganized.user.UserRepository;
import cutepush.beanorganized.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
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
    private UserService userService;

    @MockitoBean
    private UserProfileService userProfileService;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private CategoryTaskService categoryTaskService;

    @Test
    void contextLoads() {
    }

}

