package cutepush.beanorganized.api.controller.logs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LogsTestControllerTest {

    @InjectMocks
    private LogsTestController logsTestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(logsTestController).build();
    }

    @Test
    void testNormalLogs_ShouldReturnSuccessMessage() throws Exception {
        // When & Then
        mockMvc.perform(get("/v1/logs-test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logs test endpoint is working!"));
    }

    @Test
    void testErrorLogs_ShouldReturnSuccessMessage() throws Exception {
        // When & Then
        mockMvc.perform(post("/v1/logs-test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Error logs test endpoint is working!"));
    }
}