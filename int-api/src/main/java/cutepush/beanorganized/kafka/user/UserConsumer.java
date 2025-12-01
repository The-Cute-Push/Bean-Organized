package cutepush.beanorganized.kafka.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import cutepush.beanorganized.kafka.email.Email;
import cutepush.beanorganized.kafka.email.EmailService;
import cutepush.beanorganized.kafka.email.EmailTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static cutepush.beanorganized.kafka.KafkaTopics.USER_EVENTS_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserConsumer {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @KafkaListener(topics = USER_EVENTS_TOPIC, groupId = "bean-organized-group")
    public void consume(String message) {
        try {
            UserEvent event = objectMapper.readValue(message, UserEvent.class);
            log.info("User event received: {}", message);

            switch (event.eventType()) {
                case CREATE -> handleCreateEvent(event);
                case UPDATE -> handleUpdateEvent(event);
                case DELETE -> handleDeleteEvent(event);
            }
        } catch (Exception e) {
            log.error("Error processing user event from Kafka: {}", e.getMessage(), e);
            throw new UserEventProcessingException("Failed to process user event from Kafka", e);
        }
    }

    private void handleCreateEvent(UserEvent event) {
        log.info("Sending register email to user: {}", event.email());
        String subject = "Bem-vindo ao Bean Organized, " + safe(event.name()) + "!";
        String formattedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(event.creationTime());

        String innerRows = "" +
                "    <tr>" +
                "      <td style='padding:28px'>" +
                "        <h2 style='margin:0 0 10px;font-size:20px;color:#111'>Olá, " + escapeHtml(safe(event.name())) + " 👋</h2>" +
                "        <p style='margin:0 0 16px;color:#333;font-size:15px;line-height:1.55'>" +
                "          Que bom ter você por aqui! Sua conta foi criada em <strong>" + formattedDate + "</strong>." +
                "        </p>" +
                "        <div style='margin:20px 0;padding:16px;border:1px dashed #ffe1e4;background:#fff7f8;border-radius:10px;color:#5c1a1f'>" +
                "          <strong>Próximos passos</strong><br/>" +
                "          • Crie suas primeiras categorias<br/>" +
                "          • Adicione tarefas e defina prioridades<br/>" +
                "          • Ative notificações para não perder nada" +
                "        </div>" +
                "        <a href='http://localhost:8080/users/"+event.id()+"' style='display:inline-block;background:#EA1D2C;color:#fff;text-decoration:none;padding:12px 18px;border-radius:8px;font-weight:bold;'>Acessar minha conta</a>" +
                "        <p style='margin:18px 0 0;color:#666;font-size:12px'>Se você não criou esta conta, ignore este email.</p>" +
                "      </td>" +
                "    </tr>\n";

        String htmlBody = EmailTemplate.wrapWithShell(innerRows);

        Email email = new Email(event.email(), subject, htmlBody);
        emailService.sendEmail(email);
    }

    private void handleUpdateEvent(UserEvent event) {
        log.info("Sending update email to user: {}", event.email());
        String subject = "Atualização realizada com sucesso, " + safe(event.name()) + "!";
        String formattedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());

        String innerRows = "" +
                "    <tr>" +
                "      <td style='padding:28px'>" +
                "        <h2 style='margin:0 0 10px;font-size:20px;color:#111'>Olá, " + escapeHtml(safe(event.name())) + " 👋</h2>" +
                "        <p style='margin:0 0 16px;color:#333;font-size:15px;line-height:1.55'>Fizemos as alterações solicitadas em sua conta em <strong>" + formattedDate + "</strong>.</p>" +
                "        <div style='margin:20px 0;padding:16px;background:#f3f9ff;border:1px solid #dbeafe;border-radius:10px;color:#0b3b75'>" +
                "          Se alguma atualização não foi feita por você, <a href='http://localhost:8080/users/"+event.id()+"'  style='color:#0b5ed7;text-decoration:none;font-weight:bold'>fale com o suporte</a> imediatamente." +
                "        </div>" +
                "        <a href='http://localhost:8080/users/"+event.id()+"' style='display:inline-block;background:#EA1D2C;color:#fff;text-decoration:none;padding:12px 18px;border-radius:8px;font-weight:bold;'>Revisar minhas informações</a>" +
                "      </td>" +
                "    </tr>\n";

        String htmlBody = EmailTemplate.wrapWithShell(innerRows);

        Email email = new Email(event.email(), subject, htmlBody);
        emailService.sendEmail(email);
    }

    private void handleDeleteEvent(UserEvent event) {
        log.info("Sending deletion email to user: {}", event.email());
        String subject = "Sua conta foi excluída — Sentiremos sua falta, " + safe(event.name()) + "";
        String innerRows = "" +
                "    <tr>" +
                "      <td style='padding:28px'>" +
                "        <h2 style='margin:0 0 10px;font-size:20px;color:#111'>Até breve, " + escapeHtml(safe(event.name())) + "</h2>" +
                "        <p style='margin:0 0 16px;color:#333;font-size:15px;line-height:1.55'>Confirmamos a exclusão da sua conta. Seus dados serão removidos conforme nossa política de privacidade.</p>" +
                "        <div style='margin:20px 0;padding:16px;background:#fff7f0;border:1px solid #ffe1c2;border-radius:10px;color:#6a3b06'>" +
                "          Se foi engano, você pode criar sua conta novamente a qualquer momento. Estamos prontos para receber você de volta!" +
                "        </div>" +
                "        <a href='https://app.beanorganized.com/' style='display:inline-block;background:#EA1D2C;color:#fff;text-decoration:none;padding:12px 18px;border-radius:8px;font-weight:bold;'>Voltar para o site</a>" +
                "        <p style='margin:18px 0 0;color:#666;font-size:12px'>Dúvidas? <a href='mailto:suporte@beanorganized.com' style='color:#0b5ed7;text-decoration:none'>suporte@beanorganized.com</a></p>" +
                "      </td>" +
                "    </tr>\n";

        String htmlBody = EmailTemplate.wrapWithShell(innerRows);

        Email email = new Email(event.email(), subject, htmlBody);
        emailService.sendEmail(email);
    }

    private static String safe(String v) {
        return v == null ? "" : v;
    }

    private static String escapeHtml(String input) {
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
