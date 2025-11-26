package cutepush.beanorganized.postgres.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("tb_users")
public class Users {
    @Id
    private Long id;

    private String nome;
    private String email;
    private String senha;
}
