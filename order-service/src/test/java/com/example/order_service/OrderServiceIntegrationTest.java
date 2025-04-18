package com.example.order_service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.order_service.exception.ResourceNotFoundException;
// import com.example.order_service.model.Customer;
import com.example.order_service.model.Order;
// import com.example.order_service.repository.CustomerRepository;
import com.example.order_service.repository.OrderRepository;
// import com.example.order_service.service.CustomerService;
import com.example.order_service.service.OrderService;

@SpringBootTest
@ActiveProfiles("test")
@EntityScan(basePackages = "com.example.order_service.model") // Specify the package for entities
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset DB after each test
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    // @Autowired
    // private CustomerService customerService;

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
    // void createOrder_ShouldSaveAndReturnOrder() {
    // // Given
    // Customer customer = new Customer(null, "Alice Johnson",
    // "alice.johnson." + System.currentTimeMillis() + "@example.com",
    // "123 Test Lane", LocalDateTime.now(), 0);
    // Customer savedCustomer = customerService.createCustomer(customer);

    // Order order = new Order(null, savedCustomer.getId(), "Test Product", 2,
    // LocalDateTime.now());

    // // When
    // Order savedOrder = orderService.createOrder(order);

    // // Then
    // assertNotNull(savedOrder.getId());
    // assertEquals("Test Product", savedOrder.getProduct());
    // assertEquals(2, savedOrder.getQuantity());
    // }

    // @Test
    // void getAllOrders_ShouldReturnOrdersForCustomer() {
    // // Given
    // Customer customer = new Customer(null, "Bob Brown", "bob.brown." +
    // System.currentTimeMillis() + "@example.com",
    // "456 Test Road", LocalDateTime.now(), 0);
    // Customer savedCustomer = customerService.createCustomer(customer);

    // Order order1 = new Order(null, savedCustomer.getId(), "Product 1", 1,
    // LocalDateTime.now());
    // Order order2 = new Order(null, savedCustomer.getId(), "Product 2", 3,
    // LocalDateTime.now());
    // orderService.createOrder(order1);
    // orderService.createOrder(order2);

    // // When
    // List<Order> orders = orderService.getAllOrders(savedCustomer.getId(),
    // null).getContent();

    // // Then
    // assertEquals(2, orders.size());
    // }

    // @Test
    // void deleteOrder_ShouldRemoveOrder() {
    // // Given
    // Customer customer = new Customer(null, "Charlie Green",
    // "charlie.green." + System.currentTimeMillis() + "@example.com",
    // "789 Test Circle", LocalDateTime.now(), 0);
    // Customer savedCustomer = customerService.createCustomer(customer);

    // Order order = new Order(null, savedCustomer.getId(), "Test Product", 2,
    // LocalDateTime.now());
    // Order savedOrder = orderService.createOrder(order);

    // // When
    // orderService.deleteOrder(savedOrder.getId());

    // // Then
    // assertThrows(ResourceNotFoundException.class, () ->
    // orderService.getOrder(savedOrder.getId()));
    // }
}