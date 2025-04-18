package com.example.customer_service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EntityScan(basePackages = "com.example.customer_service.model") // Specify the package for entities
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset DB after each test
public class CustomerControllerAcceptanceTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void createCustomer_ShouldReturnCreatedCustomer() throws Exception {
                // Given
                Map<String, Object> customerRequest = new HashMap<>();
                customerRequest.put("name", "John Doe");
                customerRequest.put("email", "john.doe." + System.currentTimeMillis() + "@example.com"); // Unique email
                customerRequest.put("address", "123 Test Street");

                // When & Then
                mockMvc.perform(post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.name").value("John Doe"));
        }

        @Test
        void getCustomerById_ShouldReturnCustomerDetails() throws Exception {
                // Given
                Map<String, Object> customerRequest = new HashMap<>();
                customerRequest.put("name", "Jane Doe");
                customerRequest.put("email", "jane.doe." + System.currentTimeMillis() + "@example.com"); // Unique email
                customerRequest.put("address", "456 Test Avenue");

                String response = mockMvc.perform(post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerRequest)))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                Long customerId = objectMapper.readTree(response).get("id").asLong();

                // When & Then
                mockMvc.perform(get("/api/customers/{id}", customerId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(customerId))
                                .andExpect(jsonPath("$.name").value("Jane Doe"));
        }

        // @Test
        // void deleteCustomer_ShouldReturnSuccessMessage() throws Exception {
        // // Given
        // Map<String, Object> customerRequest = new HashMap<>();
        // customerRequest.put("name", "Mark Smith");
        // customerRequest.put("email", "mark.smith." + System.currentTimeMillis() +
        // "@example.com"); // Unique
        // // email
        // customerRequest.put("address", "789 Test Circle");

        // String response = mockMvc.perform(post("/api/customers")
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(objectMapper.writeValueAsString(customerRequest)))
        // .andReturn()
        // .getResponse()
        // .getContentAsString();

        // Long customerId = objectMapper.readTree(response).get("id").asLong();

        // // When & Then
        // mockMvc.perform(delete("/api/customers/{id}", customerId))
        // .andExpect(status().isOk())
        // .andExpect(content().string("Cascading delete successful")); // Update
        // expected response
        // }
}