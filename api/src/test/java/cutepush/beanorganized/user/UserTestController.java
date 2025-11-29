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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserTestController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void add_ShouldCreateUser_WhenDataIsValid() throws Exception {
        // O usuário que vamos enviar
        UserEntity userToSend = new UserEntity(null, "Lucas", "lucas@email.com", "12345678", null, null);
        // O usuário que o Service vai "retornar"
        UserEntity userSaved = new UserEntity(1L, "Lucas", "lucas@email.com", "12345678", null, null);
        when(userService.save(any(UserEntity.class))).thenReturn(userSaved);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON) // Avisa que estamos mandando JSON
                        .content(objectMapper.writeValueAsString(userToSend))) // Converte o objeto Java para String JSON
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Lucas"))
                .andExpect(jsonPath("$.email").value("lucas@email.com"));
    }
}