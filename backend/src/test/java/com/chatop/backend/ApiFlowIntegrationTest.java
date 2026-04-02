package com.chatop.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullAuthenticatedFlowShouldSucceed() throws Exception {
        String email = "it." + System.nanoTime() + "@test.com";

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "name", "Integration User",
                    "email", email,
                    "password", "test!123"
                ))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isString())
            .andReturn();

        String token = extractToken(registerResult);

        MvcResult meResult = mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(email))
            .andReturn();

        int userId = extractUserId(meResult);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/rentals")
                .file(new MockMultipartFile("picture", new byte[0]))
                .param("name", "Rental IT")
                .param("surface", "42")
                .param("price", "900")
                .param("description", "Created by integration test")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Rental created !"));

        MvcResult listResult = mockMvc.perform(get("/api/rentals")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rentals[0].name").value("Rental IT"))
            .andReturn();

        int rentalId = extractFirstRentalId(listResult);

        mockMvc.perform(get("/api/rentals/{id}", rentalId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(rentalId));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/rentals/{id}", rentalId)
                .param("name", "Rental IT Updated")
                .param("surface", "45")
                .param("price", "950")
                .param("description", "Updated by integration test")
                .header("Authorization", "Bearer " + token)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Rental updated !"));

        mockMvc.perform(get("/api/user/{id}", userId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId));

        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(Map.of(
                    "rental_id", rentalId,
                    "user_id", userId,
                    "message", "Message from integration test"
                ))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Message send with success"));
    }

    @Test
    void protectedEndpointShouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/rentals"))
            .andExpect(status().isUnauthorized());
    }

    private String extractToken(MvcResult registerResult) throws Exception {
        JsonNode json = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    private int extractUserId(MvcResult meResult) throws Exception {
        JsonNode json = objectMapper.readTree(meResult.getResponse().getContentAsString());
        return json.get("id").asInt();
    }

    private int extractFirstRentalId(MvcResult listResult) throws Exception {
        JsonNode json = objectMapper.readTree(listResult.getResponse().getContentAsString());
        return json.get("rentals").get(0).get("id").asInt();
    }
}
