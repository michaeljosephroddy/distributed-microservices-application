package com.example.customer_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.customer_service.exception.BadRequestException;
import com.example.customer_service.exception.ResourceNotFoundException;
import com.example.customer_service.model.Customer;
import com.example.customer_service.repository.CustomerRepository;
// import com.example.customer_service.repository.OrderRepository;
import com.example.customer_service.service.CustomerService;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceUnitTest {

    @Mock
    private CustomerRepository customerRepository;

    // @Mock
    // private OrderRepository orderRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("test@example.com");
        testCustomer.setAddress("123 Test Street");
        testCustomer.setCreatedAt(testDateTime);
        testCustomer.setTotalOrders(5);
    }

    @Test
    void createCustomer_WithValidCustomer_ShouldReturnSavedCustomer() {
        // Given
        Customer inputCustomer = new Customer();
        inputCustomer.setName("New Customer");
        inputCustomer.setEmail("new@example.com");
        inputCustomer.setAddress("456 New Street");

        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        Customer savedCustomer = customerService.createCustomer(inputCustomer);

        // Then
        assertNotNull(savedCustomer);
        assertEquals(testCustomer.getId(), savedCustomer.getId());
        assertEquals(testCustomer.getName(), savedCustomer.getName());
        assertEquals(testCustomer.getEmail(), savedCustomer.getEmail());
        assertEquals(testCustomer.getAddress(), savedCustomer.getAddress());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void createCustomer_WithNullCustomer_ShouldThrowBadRequestException() {
        // When & Then
        assertThrows(BadRequestException.class, () -> customerService.createCustomer(null));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void createCustomer_WithNullName_ShouldThrowBadRequestException() {
        // Given
        Customer customerWithNullName = new Customer();
        customerWithNullName.setEmail("test@example.com");
        customerWithNullName.setAddress("123 Test Street");

        // When & Then
        assertThrows(BadRequestException.class, () -> customerService.createCustomer(customerWithNullName));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void createCustomer_WithEmptyName_ShouldThrowBadRequestException() {
        // Given
        Customer customerWithEmptyName = new Customer();
        customerWithEmptyName.setName("");
        customerWithEmptyName.setEmail("test@example.com");
        customerWithEmptyName.setAddress("123 Test Street");

        // When & Then
        assertThrows(BadRequestException.class, () -> customerService.createCustomer(customerWithEmptyName));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getCustomerById_WithExistingId_ShouldReturnCustomer() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // When
        Customer foundCustomer = customerService.getCustomerById(1L);

        // Then
        assertNotNull(foundCustomer);
        assertEquals(testCustomer.getId(), foundCustomer.getId());
        assertEquals(testCustomer.getName(), foundCustomer.getName());
        assertEquals(testCustomer.getEmail(), foundCustomer.getEmail());
        assertEquals(testCustomer.getAddress(), foundCustomer.getAddress());
        assertEquals(testCustomer.getCreatedAt(), foundCustomer.getCreatedAt());
        assertEquals(testCustomer.getTotalOrders(), foundCustomer.getTotalOrders());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Given
        Long nonExistingId = 999L;
        when(customerRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> customerService.getCustomerById(nonExistingId));

        assertEquals("Customer with ID " + nonExistingId + " not found.", exception.getMessage());
        verify(customerRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void getAllCustomers_ShouldReturnAllCustomers() {
        // Given
        Customer secondCustomer = new Customer();
        secondCustomer.setId(2L);
        secondCustomer.setName("Second Customer");
        secondCustomer.setEmail("second@example.com");
        secondCustomer.setAddress("789 Second Street");
        secondCustomer.setCreatedAt(testDateTime.plusDays(1));
        secondCustomer.setTotalOrders(3);

        List<Customer> customerList = Arrays.asList(testCustomer, secondCustomer);
        when(customerRepository.findAll()).thenReturn(customerList);

        // When
        List<Customer> returnedCustomers = customerService.getAllCustomers();

        // Then
        assertNotNull(returnedCustomers);
        assertEquals(2, returnedCustomers.size());
        assertEquals(testCustomer.getId(), returnedCustomers.get(0).getId());
        assertEquals(secondCustomer.getId(), returnedCustomers.get(1).getId());
        verify(customerRepository, times(1)).findAll();
    }

    // @Test
    // void deleteCustomer_WithExistingId_ShouldDeleteCustomerAndOrders() {
    // // Given
    // when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    // // doNothing().when(orderRepository).deleteByCustomerId(1L);
    // doNothing().when(customerRepository).deleteById(1L);

    // // When
    // // customerService.deleteCustomer(1L);

    // // Then
    // verify(customerRepository, times(1)).findById(1L);
    // // verify(orderRepository, times(1)).deleteByCustomerId(1L);
    // verify(customerRepository, times(1)).deleteById(1L);
    // }

    // @Test
    // void deleteCustomer_WithNonExistingId_ShouldThrowResourceNotFoundException()
    // {
    // // Given
    // Long nonExistingId = 999L;
    // when(customerRepository.findById(nonExistingId)).thenReturn(Optional.empty());

    // // When & Then
    // ResourceNotFoundException exception =
    // assertThrows(ResourceNotFoundException.class,
    // () -> customerService.deleteCustomer(nonExistingId));

    // assertEquals("Cannot delete. Customer with ID " + nonExistingId + " not
    // found.", exception.getMessage());
    // verify(customerRepository, times(1)).findById(nonExistingId);
    // // verify(orderRepository, never()).deleteByCustomerId(anyLong());
    // verify(customerRepository, never()).deleteById(anyLong());
    // }

    @Test
    void getCustomersSorted_WithAscendingOrder_ShouldReturnCustomersInAscendingOrder() {
        // Given
        Customer olderCustomer = new Customer();
        olderCustomer.setId(2L);
        olderCustomer.setName("Older Customer");
        olderCustomer.setCreatedAt(testDateTime.minusDays(5));

        List<Customer> ascOrderedList = Arrays.asList(olderCustomer, testCustomer);
        when(customerRepository.findAllByCreatedAtAsc()).thenReturn(ascOrderedList);

        // When
        List<Customer> result = customerService.getCustomersSorted("asc");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(olderCustomer.getId(), result.get(0).getId());
        assertEquals(testCustomer.getId(), result.get(1).getId());
        verify(customerRepository, times(1)).findAllByCreatedAtAsc();
        verify(customerRepository, never()).findAllByCreatedAtDesc();
    }

    @Test
    void getCustomersSorted_WithDescendingOrder_ShouldReturnCustomersInDescendingOrder() {
        // Given
        Customer newerCustomer = new Customer();
        newerCustomer.setId(3L);
        newerCustomer.setName("Newer Customer");
        newerCustomer.setCreatedAt(testDateTime.plusDays(2));

        List<Customer> descOrderedList = Arrays.asList(newerCustomer, testCustomer);
        when(customerRepository.findAllByCreatedAtDesc()).thenReturn(descOrderedList);

        // When
        List<Customer> result = customerService.getCustomersSorted("desc");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(newerCustomer.getId(), result.get(0).getId());
        assertEquals(testCustomer.getId(), result.get(1).getId());
        verify(customerRepository, times(1)).findAllByCreatedAtDesc();
        verify(customerRepository, never()).findAllByCreatedAtAsc();
    }

    @Test
    void getCustomersSorted_WithNullOrInvalidSortDirection_ShouldDefaultToDescending() {
        // Given
        List<Customer> descOrderedList = Arrays.asList(testCustomer);
        when(customerRepository.findAllByCreatedAtDesc()).thenReturn(descOrderedList);

        // When
        List<Customer> result = customerService.getCustomersSorted("invalid");

        // Then
        assertNotNull(result);
        assertEquals(descOrderedList, result);
        verify(customerRepository, times(1)).findAllByCreatedAtDesc();
        verify(customerRepository, never()).findAllByCreatedAtAsc();
    }
}