package com.example.order_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

// import com.example.order_service.model.Customer;
import com.example.order_service.model.Order;
// import com.example.order_service.repository.CustomerRepository;
import com.example.order_service.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@EntityScan(basePackages = "com.example.order_service.model") // Specify the package for entities
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderRepositoryIntegrationTest {

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
    // void saveOrder_ShouldPersistOrder() {
    // // Given
    // Customer customer = customerRepository
    // .save(new Customer(null, "Bob", "bob." + System.currentTimeMillis() +
    // "@example.com",
    // "456 Test Road", LocalDateTime.now(), 0));
    // Order order = new Order(null, customer.getId(), "Test Product", 1,
    // LocalDateTime.now());

    // // When
    // Order savedOrder = orderRepository.save(order);

    // // Then
    // assertNotNull(savedOrder.getId());
    // assertEquals("Test Product", savedOrder.getProduct());
    // assertEquals(1, savedOrder.getQuantity());
    // }

    // @Test
    // void findByCustomerId_ShouldReturnOrders() {
    // // Given
    // Customer customer = customerRepository
    // .save(new Customer(null, "Alice", "alice." + System.currentTimeMillis() +
    // "@example.com",
    // "123 Test Lane", LocalDateTime.now(), 0));
    // orderRepository.save(new Order(null, customer.getId(), "Product 1", 1,
    // LocalDateTime.now()));
    // orderRepository.save(new Order(null, customer.getId(), "Product 2", 2,
    // LocalDateTime.now()));

    // // When
    // List<Order> orders = orderRepository.findByCustomerId(customer.getId(),
    // Pageable.unpaged()).getContent();

    // // Then
    // assertEquals(2, orders.size());
    // }
}
