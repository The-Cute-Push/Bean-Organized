package cutepush.beanorganized.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {
    @Size(max = 500)
    private String profilePhoto;
    @Size(max = 500)
    private String biography;
    @Size(max = 20)
    private String phone;
}
