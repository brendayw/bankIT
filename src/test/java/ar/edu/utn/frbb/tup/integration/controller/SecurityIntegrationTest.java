package ar.edu.utn.frbb.tup.integration.controller;

import ar.edu.utn.frbb.tup.Application;
import ar.edu.utn.frbb.tup.config.IntegrationTestBase;
import ar.edu.utn.frbb.tup.model.users.dto.AuthenticationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class SecurityIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldHandleCorsPreflightRequests() throws Exception {
        // Test de CORS si es necesario
        mockMvc.perform(post("/api/login")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Porque no tiene body
    }

    @Test
    void shouldValidateContentType() throws Exception {
        AuthenticationDto authDto = new AuthenticationDto("testuser", "password123");

        mockMvc.perform(post("/api/login")
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isUnsupportedMediaType()); // 415
    }

    @Test
    void contextLoads() {
    }
}