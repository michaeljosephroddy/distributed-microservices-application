package com.example.customer_service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.example.customer_service.exception.ResourceNotFoundException;
import com.example.customer_service.model.Customer;
import com.example.customer_service.repository.CustomerRepository;
import com.example.customer_service.service.CustomerService;

@SpringBootTest
@ActiveProfiles("test") // Activates the "test" profile
@EntityScan(basePackages = "com.example.customer_service.model") // Specify the package for entities
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset DB after each test
public class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    // @Autowired
    // private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // @Mock
    // private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        // orderRepository.deleteAll(); // Clear orders before each test
        customerRepository.deleteAll(); // Clear customers before each test
    }

    @Test
    void createCustomer_ShouldSaveAndReturnCustomer() {
        // Given
        Customer customer = new Customer(null, "John Doe", "john.doe." + System.currentTimeMillis() + "@example.com",
                "123 Test Street", LocalDateTime.now(), 0);

        // When
        Customer savedCustomer = customerService.createCustomer(customer);

        // Then
        assertNotNull(savedCustomer.getId());
        assertEquals("John Doe", savedCustomer.getName());
        assertTrue(savedCustomer.getEmail().startsWith("john.doe."));
    }

    @Test
    void getCustomerById_ShouldReturnCustomer() {
        // Given
        Customer customer = new Customer(null, "Jane Doe", "jane.doe." + System.currentTimeMillis() + "@example.com",
                "456 Test Avenue", LocalDateTime.now(), 0);
        Customer savedCustomer = customerService.createCustomer(customer);

        // When
        Customer foundCustomer = customerService.getCustomerById(savedCustomer.getId());

        // Then
        assertNotNull(foundCustomer);
        assertEquals("Jane Doe", foundCustomer.getName());
        assertTrue(foundCustomer.getEmail().startsWith("jane.doe."));
    }

    // @Test
    // void deleteCustomer_ShouldRemoveCustomer() {
    // // Given
    // Customer customer = new Customer(null, "Mark Smith",
    // "mark.smith." + System.currentTimeMillis() + "@example.com",
    // "789 Test Blvd", LocalDateTime.now(), 0);
    // Customer savedCustomer = customerService.createCustomer(customer);

    // // When
    // customerService.deleteCustomer(savedCustomer.getId());

    // // Then
    // assertThrows(ResourceNotFoundException.class, () ->
    // customerService.getCustomerById(savedCustomer.getId()));
    // }
}