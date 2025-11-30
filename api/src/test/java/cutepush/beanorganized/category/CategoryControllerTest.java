package cutepush.beanorganized.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import cutepush.beanorganized.user.UserEntity;
import cutepush.beanorganized.user.UserService;
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
public class CategoryControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void create_ShouldCreateCategory_WhenUserExists() throws Exception {
        Long userId = 1L;
        UserEntity user = new UserEntity(userId, "Lucas", "lucas@email.com", "pass", null, null);
        CategoryRequest req = new CategoryRequest("Work", "#ff0000");

        when(userService.findById(eq(userId))).thenReturn(Optional.of(user));

        CategoryEntity saved = new CategoryEntity(10L, user, req.getName(), req.getColor());
        when(categoryService.save(any(CategoryEntity.class))).thenReturn(saved);

        mockMvc.perform(post("/users/{userId}/categories", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Work"));
    }

    @Test
    void create_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 1L;
        CategoryRequest req = new CategoryRequest("Work", "#ff0000");
        when(userService.findById(eq(userId))).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/{userId}/categories", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void list_ShouldReturnCategories_WhenUserExists() throws Exception {
        Long userId = 1L;
        UserEntity user = new UserEntity(userId, "Lucas", "lucas@email.com", "pass", null, null);
        CategoryEntity c = new CategoryEntity(2L, user, "Home", "#00ff00");

        when(userService.findById(eq(userId))).thenReturn(Optional.of(user));
        when(categoryService.findAllByUserId(eq(userId))).thenReturn(java.util.List.of(c));

        mockMvc.perform(get("/users/{userId}/categories", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].name").value("Home"));
    }

    @Test
    void get_ShouldReturnCategory_WhenExists() throws Exception {
        Long userId = 1L;
        Long cid = 3L;
        UserEntity user = new UserEntity(userId, "Lucas", "lucas@email.com", "pass", null, null);
        CategoryEntity c = new CategoryEntity(cid, user, "Play", "#0000ff");

        when(userService.findById(eq(userId))).thenReturn(Optional.of(user));
        when(categoryService.findByIdAndUserId(eq(cid), eq(userId))).thenReturn(Optional.of(c));

        mockMvc.perform(get("/users/{userId}/categories/{categoryId}", userId, cid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cid))
                .andExpect(jsonPath("$.name").value("Play"));
    }

    @Test
    void get_ShouldReturn404_WhenNotFound() throws Exception {
        Long userId = 1L;
        Long cid = 3L;
        when(userService.findById(eq(userId))).thenReturn(Optional.of(new UserEntity()));
        when(categoryService.findByIdAndUserId(eq(cid), eq(userId))).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{userId}/categories/{categoryId}", userId, cid))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ShouldUpdateCategory_WhenExists() throws Exception {
        Long userId = 1L;
        Long cid = 4L;
        UserEntity user = new UserEntity(userId, "Lucas", "lucas@email.com", "pass", null, null);
        CategoryEntity existing = new CategoryEntity(cid, user, "Old", "#111111");
        CategoryRequest req = new CategoryRequest("New", "#222222");

        when(userService.findById(eq(userId))).thenReturn(Optional.of(user));
        when(categoryService.findByIdAndUserId(eq(cid), eq(userId))).thenReturn(Optional.of(existing));

        CategoryEntity updated = new CategoryEntity(cid, user, req.getName(), req.getColor());
        when(categoryService.save(any(CategoryEntity.class))).thenReturn(updated);

        mockMvc.perform(put("/users/{userId}/categories/{categoryId}", userId, cid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New"));
    }

    @Test
    void delete_ShouldRemoveCategory_WhenExists() throws Exception {
        Long userId = 1L;
        Long cid = 5L;
        UserEntity user = new UserEntity(userId, "Lucas", "lucas@email.com", "pass", null, null);
        CategoryEntity existing = new CategoryEntity(cid, user, "X", "#333333");

        when(userService.findById(eq(userId))).thenReturn(Optional.of(user));
        when(categoryService.findByIdAndUserId(eq(cid), eq(userId))).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/users/{userId}/categories/{categoryId}", userId, cid))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_ShouldReturn404_WhenNotFound() throws Exception {
        Long userId = 1L;
        Long cid = 5L;

        when(userService.findById(eq(userId))).thenReturn(Optional.of(new UserEntity()));
        when(categoryService.findByIdAndUserId(eq(cid), eq(userId))).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/{userId}/categories/{categoryId}", userId, cid))
                .andExpect(status().isNotFound());
    }
}

