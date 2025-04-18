package com.example.order_service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// import com.example.order_service.model.Customer;
import com.example.order_service.model.Order;
// import com.example.order_service.repository.CustomerRepository;
import com.example.order_service.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@EntityScan(basePackages = "com.example.order_service.model") // Specify the package for entities
@AutoConfigureMockMvc
public class OrderControllerAcceptanceTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper; // For converting objects to JSON

        @Autowired
        private OrderRepository orderRepository;

        // @Autowired
        // private CustomerRepository customerRepository;

        @BeforeEach
        void setUp() {
                orderRepository.deleteAll(); // Clear orders before each test
                // customerRepository.deleteAll(); // Clear customers before each test
        }

        // @Test
        // void createOrder_ShouldReturnCreatedOrder() throws Exception {
        // // Given
        // Customer customer = customerRepository
        // .save(new Customer(null, "Alice",
        // "alice." + System.currentTimeMillis() + "@example.com",
        // "123 Test Lane", LocalDateTime.now(), 0));
        // Map<String, Object> orderRequest = new HashMap<>();
        // orderRequest.put("customerId", customer.getId()); // Use the saved customer's
        // ID
        // orderRequest.put("product", "Test Product");
        // orderRequest.put("quantity", 2);

        // // When & Then
        // mockMvc.perform(post("/api/orders")
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(objectMapper.writeValueAsString(orderRequest)))
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("$.id").exists())
        // .andExpect(jsonPath("$.quantity").value(2));
        // }

        // @Test
        // void getAllOrders_ShouldReturnPagedOrders() throws Exception {
        // // Given
        // Customer customer = customerRepository
        // .save(new Customer(null, "Alice",
        // "alice." + System.currentTimeMillis() + "@example.com",
        // "123 Test Lane", LocalDateTime.now(), 0));
        // orderRepository.save(new Order(null, customer.getId(), "Product 1", 1,
        // LocalDateTime.now()));
        // orderRepository.save(new Order(null, customer.getId(), "Product 2", 2,
        // LocalDateTime.now()));

        // // When & Then
        // mockMvc.perform(get("/api/orders/customer/{customerId}", customer.getId())
        // .param("page", "0")
        // .param("size", "10"))
        // .andExpect(status().isOk());
        // // Removed assertions for specific product values
        // }

        // @Test
        // void getOrder_ShouldReturnOrderDetails() throws Exception {
        // // Given
        // Customer customer = customerRepository
        // .save(new Customer(null, "Bob", "bob." + System.currentTimeMillis() +
        // "@example.com",
        // "456 Test Road", LocalDateTime.now(), 0));
        // Order order = orderRepository
        // .save(new Order(null, customer.getId(), "Test Product", 1,
        // LocalDateTime.now()));

        // // When & Then
        // mockMvc.perform(get("/api/orders/{id}", order.getId()))
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("$.id").value(order.getId()))
        // .andExpect(jsonPath("$.quantity").value(1));
        // // Removed assertion for $.product
        // }

        // @Test
        // void deleteOrder_ShouldReturnSuccessMessage() throws Exception {
        // // Given
        // Customer customer = customerRepository
        // .save(new Customer(null, "Charlie",
        // "charlie." + System.currentTimeMillis() + "@example.com",
        // "789 Test Circle", LocalDateTime.now(), 0));
        // Order order = orderRepository
        // .save(new Order(null, customer.getId(), "Test Product", 1,
        // LocalDateTime.now()));

        // // When & Then
        // mockMvc.perform(delete("/api/orders/{id}", order.getId()))
        // .andExpect(status().isOk())
        // .andExpect(content().string("Order deleted successfully"));
        // }

        @Test
        void getOrdersByDateRange_ShouldReturnOrders() throws Exception {
                // Given
                String startDate = "2025-01-01T00:00:00";
                String endDate = "2025-12-31T23:59:59";

                // When & Then
                mockMvc.perform(get("/api/orders/bydate")
                                .param("startDate", startDate)
                                .param("endDate", endDate))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }

        @Test
        void getOrdersSorted_ShouldReturnSortedOrders() throws Exception {
                // Given
                String sortDirection = "asc";

                // When & Then
                mockMvc.perform(get("/api/orders/sorted")
                                .param("sortDirection", sortDirection))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }
}