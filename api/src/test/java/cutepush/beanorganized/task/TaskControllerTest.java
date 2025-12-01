package cutepush.beanorganized.task;

import cutepush.beanorganized.user.UserEntity;
import cutepush.beanorganized.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void create_ShouldCreateTask_WhenUserExists() throws Exception {
        Long userId = 1L;
        UserEntity user = new UserEntity(userId, "Lucas", "lucas@email.com", "12345678", null, null);
        TaskRequest req = new TaskRequest("Pay bills", "Electricity and water", Instant.parse("2025-12-01T00:00:00Z"), "pendente");

        when(userService.findById(eq(userId))).thenReturn(Optional.of(user));
        TaskEntity saved = new TaskEntity(10L, user, req.getTitle(), req.getDescription(), Instant.now(), req.getDueDate(), req.getStatus());
        when(taskService.save(any(TaskEntity.class))).thenReturn(saved);

        mockMvc.perform(post("/users/{userId}/tasks", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.title").value("Pay bills"))
                .andExpect(jsonPath("$.status").value("pendente"));
    }

    @Test
    void create_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 99L;
        TaskRequest req = new TaskRequest("Pay bills", "Electricity and water", null, "pendente");
        when(userService.findById(eq(userId))).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/{userId}/tasks", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void list_ShouldReturnTasks_ForUser() throws Exception {
        Long userId = 1L;
        when(userService.findById(eq(userId))).thenReturn(Optional.of(new UserEntity()));
        TaskEntity t1 = new TaskEntity(1L, null, "A", "a", Instant.now(), null, "pendente");
        TaskEntity t2 = new TaskEntity(2L, null, "B", "b", Instant.now(), null, "concluída");
        when(taskService.findAllByUserId(eq(userId), any())).thenReturn(new PageImpl<>(List.of(t1, t2), PageRequest.of(0,20), 2));

        mockMvc.perform(get("/users/{userId}/tasks", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].status").value("concluída"));
    }

    @Test
    void list_WithFilters_ShouldReturnFilteredTasks() throws Exception {
        Long userId = 1L;
        when(userService.findById(eq(userId))).thenReturn(Optional.of(new UserEntity()));
        TaskEntity t = new TaskEntity(3L, null, "Pay rent", "desc", Instant.now(), Instant.parse("2025-12-01T00:00:00Z"), "pendente");
        when(taskService.findByUserAndFilters(eq(userId), eq("rent"), eq(5L), any(), any(), any())).thenReturn(new PageImpl<>(List.of(t), PageRequest.of(0,20), 1));

        mockMvc.perform(get("/users/{userId}/tasks", userId)
                        .param("title", "rent")
                        .param("categoryId", "5")
                        .param("from", "2025-01-01T00:00:00Z")
                        .param("to", "2025-12-31T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(3L))
                .andExpect(jsonPath("$.content[0].title").value("Pay rent"));
    }

    @Test
    void get_ShouldReturn404_WhenTaskNotFound() throws Exception {
        Long userId = 1L;
        Long taskId = 9L;
        when(userService.findById(eq(userId))).thenReturn(Optional.of(new UserEntity()));
        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{userId}/tasks/{taskId}", userId, taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ShouldUpdateTask_WhenExists() throws Exception {
        Long userId = 1L;
        Long taskId = 5L;
        UserEntity user = new UserEntity(userId, "Lucas", "lucas@email.com", "12345678", null, null);
        TaskEntity existing = new TaskEntity(taskId, user, "Old", "desc", Instant.now(), null, "pendente");
        TaskRequest req = new TaskRequest("New", "new desc", Instant.parse("2025-12-31T00:00:00Z"), "em andamento");

        when(userService.findById(eq(userId))).thenReturn(Optional.of(user));
        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(existing));
        TaskEntity updated = new TaskEntity(taskId, user, req.getTitle(), req.getDescription(), existing.getDateCreation(), req.getDueDate(), req.getStatus());
        when(taskService.save(any(TaskEntity.class))).thenReturn(updated);

        mockMvc.perform(put("/users/{userId}/tasks/{taskId}", userId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New"))
                .andExpect(jsonPath("$.status").value("em andamento"));
    }

    @Test
    void delete_ShouldRemoveTask_WhenExists() throws Exception {
        Long userId = 1L;
        Long taskId = 5L;
        when(userService.findById(eq(userId))).thenReturn(Optional.of(new UserEntity()));
        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(new TaskEntity()));

        mockMvc.perform(delete("/users/{userId}/tasks/{taskId}", userId, taskId))
                .andExpect(status().isNoContent());
    }
}
