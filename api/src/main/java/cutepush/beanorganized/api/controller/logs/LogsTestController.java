package cutepush.beanorganized.api.controller.logs;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/logs-test")
@Log4j2
public class LogsTestController {

    @GetMapping
    public String testNormalLogs() {
        log.info("This is an info log for testing purposes.");
        log.warn("This is a warning log for testing purposes.");
        log.error("This is an error log for testing purposes.");
        return "Logs test endpoint is working!";
    }

    @PostMapping
    public String testErrorLogs() {
        try {
            throw new RuntimeException("This is a test exception for error logging.");
        } catch (RuntimeException e) {
            log.error("An exception occurred during logs test.", e);
        }
        return "Error logs test endpoint is working!";
    }
}
