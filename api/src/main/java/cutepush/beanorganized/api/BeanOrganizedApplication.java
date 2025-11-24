package cutepush.beanorganized.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication(scanBasePackages = {
    "cutepush.beanorganized.api",
    "cutepush.beanorganized.domain"
})
@EnableJdbcRepositories(basePackages = "cutepush.beanorganized.domain.repository")
@EntityScan(basePackages = "cutepush.beanorganized.domain.entity")
public class BeanOrganizedApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeanOrganizedApplication.class, args);
    }

}

