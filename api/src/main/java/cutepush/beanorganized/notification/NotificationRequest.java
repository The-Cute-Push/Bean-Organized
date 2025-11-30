package cutepush.beanorganized.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotNull
    private Instant dateHour;

    @NotBlank
    @Size(max = 50)
    private String type; // email, push, popup

    @NotNull
    private Boolean sent;
}
