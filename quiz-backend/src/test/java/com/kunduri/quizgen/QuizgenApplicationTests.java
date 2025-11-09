package com.kunduri.quizgen;

import com.kunduri.quizgen.service.AIService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource(properties = {
        "openai.api.key=test-key",
        "openai.api.url=http://localhost:8080"
})
class QuizgenApplicationTests {

    @MockitoBean
    private AIService aiService;

    @Test
    void contextLoads() {
        // Context should load successfully
    }
}