package com.example.customer_service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.example.customer_service.model.Customer;
import com.example.customer_service.repository.CustomerRepository;
// import com.example.customer_service.repository.OrderRepository;

@SpringBootTest
@ActiveProfiles("test")
@EntityScan(basePackages = "com.example.customer_service.model") // Specify the package for entities
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset DB after each test
public class CustomerRepositoryIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    // @Autowired
    // private OrderRepository orderRepository;

    // @Mock
    // private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        // orderRepository.deleteAll(); // Clear orders before each test
        customerRepository.deleteAll(); // Clear customers before each test
    }

    @Test
    void saveCustomer_ShouldPersistCustomer() {
        // Given
        Customer customer = new Customer(null, "John Doe", "john.doe." + System.currentTimeMillis() + "@example.com",
                "123 Test Street", LocalDateTime.now(), 0);

        // When
        Customer savedCustomer = customerRepository.save(customer);

        // Then
        assertNotNull(savedCustomer.getId());
        assertEquals("John Doe", savedCustomer.getName());
        assertTrue(savedCustomer.getEmail().startsWith("john.doe."));
    }

    @Test
    void findById_ShouldReturnCustomer() {
        // Given
        Customer customer = new Customer(null, "Jane Doe", "jane.doe." + System.currentTimeMillis() + "@example.com",
                "456 Test Avenue", LocalDateTime.now(), 0);
        Customer savedCustomer = customerRepository.save(customer);

        // When
        Customer foundCustomer = customerRepository.findById(savedCustomer.getId()).orElse(null);

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
    // Customer savedCustomer = customerRepository.save(customer);

    // // When
    // customerRepository.deleteById(savedCustomer.getId());

    // // Then
    // assertFalse(customerRepository.findById(savedCustomer.getId()).isPresent());
    // }

    @Test
    void findAll_ShouldReturnAllCustomers() {
        // Given
        Customer customer1 = new Customer(null, "Alice", "alice." + System.currentTimeMillis() + "@example.com",
                "123 Test Lane", LocalDateTime.now(), 0);
        Customer customer2 = new Customer(null, "Bob", "bob." + System.currentTimeMillis() + "@example.com",
                "456 Test Road", LocalDateTime.now(), 0);
        customerRepository.save(customer1);
        customerRepository.save(customer2);

        // When
        List<Customer> customers = customerRepository.findAll();

        // Then
        assertEquals(2, customers.size());
    }
}