package cutepush.beanorganized.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
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
public class UserProfileTestController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private UserProfileController userProfileController;

    @Mock
    private UserService userService;

    @Mock
    private UserProfileService userProfileService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userProfileController).build();
    }

    @Test
    void create_ShouldCreateProfile_WhenDataIsValidAndUserExists() throws Exception {
        Long userId = 1L;
        UserEntity user = new UserEntity(userId, "Lucas", "lucas@email.com", "12345678", null, null);
        UserProfileRequest req = new UserProfileRequest("http://img/pic.png", "bio here", "+55 11 99999-9999");

        when(userService.findById(eq(userId))).thenReturn(Optional.of(user));
        when(userProfileService.findByUserId(eq(userId))).thenReturn(Optional.empty());

        UserProfileEntity saved = new UserProfileEntity(userId, user, req.getProfilePhoto(), req.getBiography(), req.getPhone());
        when(userProfileService.save(any(UserProfileEntity.class))).thenReturn(saved);

        mockMvc.perform(post("/users/{userId}/profile", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.profilePhoto").value("http://img/pic.png"))
                .andExpect(jsonPath("$.biography").value("bio here"))
                .andExpect(jsonPath("$.phone").value("+55 11 99999-9999"));
    }

    @Test
    void create_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 99L;
        UserProfileRequest req = new UserProfileRequest("http://img/pic.png", "bio here", "+55 11 99999-9999");

        when(userService.findById(eq(userId))).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/{userId}/profile", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn409_WhenProfileAlreadyExists() throws Exception {
        Long userId = 1L;
        UserEntity user = new UserEntity(userId, "Lucas", "lucas@email.com", "12345678", null, null);
        UserProfileRequest req = new UserProfileRequest("http://img/pic.png", "bio here", "+55 11 99999-9999");

        when(userService.findById(eq(userId))).thenReturn(Optional.of(user));
        when(userProfileService.findByUserId(eq(userId))).thenReturn(Optional.of(new UserProfileEntity()));

        mockMvc.perform(post("/users/{userId}/profile", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void get_ShouldReturnProfile_WhenExists() throws Exception {
        Long userId = 1L;
        UserEntity user = new UserEntity(userId, "Lucas", "lucas@email.com", "12345678", null, null);
        UserProfileEntity profile = new UserProfileEntity(userId, user, "http://img/pic.png", "bio here", "+55 11 99999-9999");

        when(userProfileService.findByUserId(eq(userId))).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/users/{userId}/profile", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.profilePhoto").value("http://img/pic.png"));
    }

    @Test
    void get_ShouldReturn404_WhenProfileNotFound() throws Exception {
        Long userId = 1L;
        when(userProfileService.findByUserId(eq(userId))).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{userId}/profile", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ShouldUpdateProfile_WhenExists() throws Exception {
        Long userId = 1L;
        UserEntity user = new UserEntity(userId, "Lucas", "lucas@email.com", "12345678", null, null);
        UserProfileEntity existing = new UserProfileEntity(userId, user, "http://img/pic.png", "bio here", "+55 11 99999-9999");
        UserProfileRequest req = new UserProfileRequest("http://img/new.png", "new bio", "+55 21 90000-0000");

        when(userService.findById(eq(userId))).thenReturn(Optional.of(user));
        when(userProfileService.findByUserId(eq(userId))).thenReturn(Optional.of(existing));

        UserProfileEntity updated = new UserProfileEntity(userId, user, req.getProfilePhoto(), req.getBiography(), req.getPhone());
        when(userProfileService.save(any(UserProfileEntity.class))).thenReturn(updated);

        mockMvc.perform(put("/users/{userId}/profile", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profilePhoto").value("http://img/new.png"))
                .andExpect(jsonPath("$.biography").value("new bio"))
                .andExpect(jsonPath("$.phone").value("+55 21 90000-0000"));
    }

    @Test
    void delete_ShouldRemoveProfile_WhenExists() throws Exception {
        Long userId = 1L;
        when(userProfileService.findByUserId(eq(userId))).thenReturn(Optional.of(new UserProfileEntity()));

        mockMvc.perform(delete("/users/{userId}/profile", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_ShouldReturn404_WhenNotExists() throws Exception {
        Long userId = 1L;
        when(userProfileService.findByUserId(eq(userId))).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/{userId}/profile", userId))
                .andExpect(status().isNotFound());
    }
}
