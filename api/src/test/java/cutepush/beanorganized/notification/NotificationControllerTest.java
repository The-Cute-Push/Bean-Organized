package cutepush.beanorganized.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import cutepush.beanorganized.task.TaskEntity;
import cutepush.beanorganized.task.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TaskService taskService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Register JavaTimeModule so Instant is serialized/deserialized correctly
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Configure MockMvc to use the same ObjectMapper for request/response bodies
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setMessageConverters(jacksonConverter)
                .build();
    }

    @Test
    void create_ShouldCreateNotification_WhenTaskExists() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        TaskEntity task = new TaskEntity();
        task.setId(taskId);

        NotificationRequest req = new NotificationRequest(Instant.now(), "email", true);

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(task));

        NotificationEntity saved = new NotificationEntity(1L, task, req.getDateHour(), req.getType(), req.getSent());
        when(notificationService.save(any(NotificationEntity.class))).thenReturn(saved);

        mockMvc.perform(post("/users/{userId}/tasks/{taskId}/notifications", userId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.type").value("email"));
    }

    @Test
    void create_ShouldReturn404_WhenTaskNotFound() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        NotificationRequest req = new NotificationRequest(Instant.now(), "email", true);

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/{userId}/tasks/{taskId}/notifications", userId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void list_ShouldReturnNotifications_WhenTaskExists() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        TaskEntity task = new TaskEntity();
        task.setId(taskId);

        NotificationEntity n1 = new NotificationEntity(1L, task, Instant.now(), "email", false);

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(task));
        when(notificationService.findAllByTaskId(eq(taskId))).thenReturn(java.util.List.of(n1));

        mockMvc.perform(get("/users/{userId}/tasks/{taskId}/notifications", userId, taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].type").value("email"));
    }

    @Test
    void get_ShouldReturnNotification_WhenExists() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        Long nid = 5L;
        TaskEntity task = new TaskEntity();
        task.setId(taskId);

        NotificationEntity n = new NotificationEntity(nid, task, Instant.now(), "push", false);

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(task));
        when(notificationService.findByIdAndTaskId(eq(nid), eq(taskId))).thenReturn(Optional.of(n));

        mockMvc.perform(get("/users/{userId}/tasks/{taskId}/notifications/{notificationId}", userId, taskId, nid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.type").value("push"));
    }

    @Test
    void get_ShouldReturn404_WhenNotFound() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        Long nid = 5L;

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(new TaskEntity()));
        when(notificationService.findByIdAndTaskId(eq(nid), eq(taskId))).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{userId}/tasks/{taskId}/notifications/{notificationId}", userId, taskId, nid))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ShouldUpdateNotification_WhenExists() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        Long nid = 5L;
        TaskEntity task = new TaskEntity();
        task.setId(taskId);

        NotificationEntity existing = new NotificationEntity(nid, task, Instant.now(), "email", false);
        NotificationRequest req = new NotificationRequest(Instant.now(), "push", true);

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(task));
        when(notificationService.findByIdAndTaskId(eq(nid), eq(taskId))).thenReturn(Optional.of(existing));

        NotificationEntity updated = new NotificationEntity(nid, task, req.getDateHour(), req.getType(), req.getSent());
        when(notificationService.save(any(NotificationEntity.class))).thenReturn(updated);

        mockMvc.perform(put("/users/{userId}/tasks/{taskId}/notifications/{notificationId}", userId, taskId, nid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("push"))
                .andExpect(jsonPath("$.sent").value(true));
    }

    @Test
    void delete_ShouldRemoveNotification_WhenExists() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        Long nid = 5L;
        TaskEntity task = new TaskEntity();
        task.setId(taskId);

        NotificationEntity existing = new NotificationEntity(nid, task, Instant.now(), "email", false);

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(task));
        when(notificationService.findByIdAndTaskId(eq(nid), eq(taskId))).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/users/{userId}/tasks/{taskId}/notifications/{notificationId}", userId, taskId, nid))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_ShouldReturn404_WhenNotFound() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        Long nid = 5L;

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(new TaskEntity()));
        when(notificationService.findByIdAndTaskId(eq(nid), eq(taskId))).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/{userId}/tasks/{taskId}/notifications/{notificationId}", userId, taskId, nid))
                .andExpect(status().isNotFound());
    }
}
