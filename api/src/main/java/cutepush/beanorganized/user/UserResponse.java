package cutepush.beanorganized.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

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
    private List<Task> tasks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        private Long id;
        private String profilePhoto;
        private String biography;
        private String phone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Task {
        private Long id;
        private String title;
        private String description;
        private Instant dateCreation;
        private Instant dueDate;
        private String status;
    }
}
