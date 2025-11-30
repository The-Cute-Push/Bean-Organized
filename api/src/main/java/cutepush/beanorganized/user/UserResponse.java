package cutepush.beanorganized.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Instant dateCreation;
    private Timestamp lastLogin;
    private Profile profile;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        private Long id;
        private String profilePhoto;
        private String biography;
        private String phone;
    }
}
