package cutepush.beanorganized.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("tb_user")
public class UserEntity {
    @Id
    private Long id;

    private String name;
    private String email;
    private String password;
}
