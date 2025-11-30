package cutepush.beanorganized.categorytasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import cutepush.beanorganized.category.CategoryEntity;
import cutepush.beanorganized.category.CategoryService;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryTaskControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CategoryTaskController categoryTaskController;

    @Mock
    private CategoryTaskService categoryTaskService;

    @Mock
    private TaskService taskService;

    @Mock
    private CategoryService categoryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryTaskController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void create_ShouldCreateAssociation_WhenTaskAndCategoryExist() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        Long categoryId = 20L;

        TaskEntity task = new TaskEntity(); task.setId(taskId);
        CategoryEntity category = new CategoryEntity(); category.setId(categoryId);

        CategoryTaskRequest req = new CategoryTaskRequest(categoryId);

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(task));
        when(categoryService.findByIdAndUserId(eq(categoryId), eq(userId))).thenReturn(Optional.of(category));
        when(categoryTaskService.findByCategoryIdAndTaskId(eq(categoryId), eq(taskId))).thenReturn(Optional.empty());

        CategoryTaskEntity saved = new CategoryTaskEntity(1L, category, task);
        when(categoryTaskService.save(any(CategoryTaskEntity.class))).thenReturn(saved);

        mockMvc.perform(post("/users/{userId}/tasks/{taskId}/categories", userId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.categoryId").value(categoryId));
    }

    @Test
    void create_ShouldReturn404_WhenTaskNotFound() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        Long categoryId = 20L;

        CategoryTaskRequest req = new CategoryTaskRequest(categoryId);
        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/{userId}/tasks/{taskId}/categories", userId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn404_WhenCategoryNotFound() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        Long categoryId = 20L;

        CategoryTaskRequest req = new CategoryTaskRequest(categoryId);
        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(new TaskEntity()));
        when(categoryService.findByIdAndUserId(eq(categoryId), eq(userId))).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/{userId}/tasks/{taskId}/categories", userId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturnExisting_WhenDuplicate() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        Long categoryId = 20L;

        TaskEntity task = new TaskEntity(); task.setId(taskId);
        CategoryEntity category = new CategoryEntity(); category.setId(categoryId);

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(task));
        when(categoryService.findByIdAndUserId(eq(categoryId), eq(userId))).thenReturn(Optional.of(category));

        CategoryTaskEntity existing = new CategoryTaskEntity(2L, category, task);
        when(categoryTaskService.findByCategoryIdAndTaskId(eq(categoryId), eq(taskId))).thenReturn(Optional.of(existing));

        CategoryTaskRequest req = new CategoryTaskRequest(categoryId);

        mockMvc.perform(post("/users/{userId}/tasks/{taskId}/categories", userId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.categoryId").value(categoryId));
    }

    @Test
    void list_ShouldReturnAssociations_WhenTaskExists() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        TaskEntity task = new TaskEntity(); task.setId(taskId);
        CategoryEntity category = new CategoryEntity(); category.setId(20L);

        CategoryTaskEntity cte = new CategoryTaskEntity(3L, category, task);

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(task));
        when(categoryTaskService.findAllByTaskId(eq(taskId))).thenReturn(java.util.List.of(cte));

        mockMvc.perform(get("/users/{userId}/tasks/{taskId}/categories", userId, taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L));
    }

    @Test
    void get_ShouldReturnAssociation_WhenExists() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        Long ctid = 5L;
        TaskEntity task = new TaskEntity(); task.setId(taskId);
        CategoryEntity category = new CategoryEntity(); category.setId(20L);

        CategoryTaskEntity cte = new CategoryTaskEntity(ctid, category, task);

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(task));
        when(categoryTaskService.findByIdAndTaskId(eq(ctid), eq(taskId))).thenReturn(Optional.of(cte));

        mockMvc.perform(get("/users/{userId}/tasks/{taskId}/categories/{categoryTaskId}", userId, taskId, ctid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ctid));
    }

    @Test
    void delete_ShouldRemoveAssociation_WhenExists() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        Long ctid = 5L;
        TaskEntity task = new TaskEntity(); task.setId(taskId);
        CategoryEntity category = new CategoryEntity(); category.setId(20L);

        CategoryTaskEntity existing = new CategoryTaskEntity(ctid, category, task);

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(task));
        when(categoryTaskService.findByIdAndTaskId(eq(ctid), eq(taskId))).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/users/{userId}/tasks/{taskId}/categories/{categoryTaskId}", userId, taskId, ctid))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_ShouldReturn404_WhenNotFound() throws Exception {
        Long userId = 1L;
        Long taskId = 10L;
        Long ctid = 5L;

        when(taskService.findByIdAndUserId(eq(taskId), eq(userId))).thenReturn(Optional.of(new TaskEntity()));
        when(categoryTaskService.findByIdAndTaskId(eq(ctid), eq(taskId))).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/{userId}/tasks/{taskId}/categories/{categoryTaskId}", userId, taskId, ctid))
                .andExpect(status().isNotFound());
    }
}
