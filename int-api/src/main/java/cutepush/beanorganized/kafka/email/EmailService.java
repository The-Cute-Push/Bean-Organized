package cutepush.beanorganized.kafka.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendEmail(Email email) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom("noreply@beanorganized.com");
            helper.setTo(email.to());
            helper.setSubject(email.subject());
            helper.setText(email.body(), true); // enable HTML
            javaMailSender.send(mimeMessage);
            log.info("Email sent to {}", email.to());
        } catch (Exception e) {
            log.error("Failed to send email to {}", email.to(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
